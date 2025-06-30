package com.recykal.audit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recykal.audit.annotations.Auditable;
import com.recykal.audit.dto.ApiResponse;
import com.recykal.audit.dto.AuditEvent;
import com.recykal.audit.dto.AuditProperties;
import com.recykal.audit.dto.EntityMatching;
import com.recykal.audit.enums.ActionType;
import com.recykal.audit.utils.CloneUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class AuditLoggingHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuditLoggingHandler.class);
    private final AmqpTemplate rabbitTemplate;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;
    private final EntityMatching entityMatcher;
    private final AuditProperties auditProperties;

    @Autowired
    public AuditLoggingHandler(AmqpTemplate rabbitTemplate, EntityManager entityManager, ObjectMapper objectMapper, EntityMatching entityMatcher, AuditProperties auditProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.entityManager = entityManager;
        this.objectMapper = objectMapper;
        this.entityMatcher = entityMatcher;
        this.auditProperties = auditProperties;
    }

    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        HttpServletRequest request = null;
        try {
            request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (Exception e) {
            logger.warn("Unable to fetch HttpServletRequest: {}", e.getMessage());
        }

        logger.info("Method: {} is intercepted by audit logging aspect", method.getName());

        String uri = request != null ? request.getRequestURI() : null;
        String entityName = "UNKNOWN";
        String entityId = null;
        Object rawDataBefore = null;
        ActionType actionType = ActionType.UNKNOWN;

        try {
            entityName = extractEntityName(joinPoint.getArgs());
            entityId = extractEntityId(joinPoint.getArgs());
            actionType = determineAction(method, entityId);

            if ("UNKNOWN".equals(entityName) && uri != null) {
                String[] parts = uri.split("/");
                for (int i = 0; i < parts.length; i++) {
                    if ("api".equals(parts[i]) && i + 1 < parts.length) {
                        entityName = entityMatcher.getEntityName(parts[i + 1]);
                        break;
                    }
                }
                logger.debug("Entity name extracted from URI: {} -> {}", uri, entityName);
            }

            if (entityId != null) {
                Object originalEntity = getEntityByNameAndId(entityName, entityId, entityManager);
                rawDataBefore = CloneUtils.deepCopy(objectMapper, originalEntity);
            }

        } catch (Exception e) {
            logger.warn("Error during entity extraction: {}", e.getMessage());
        }

        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            logger.error("Exception in business method: {}, entity={}, id={}, error={}", method.getName(), entityName, entityId, ex.getMessage());
            throw ex;
        }

        try {
            Object responseBody = extractResponseBody(result);

            if (ActionType.DELETE.equals(actionType)) {
                responseBody = null;
            }

            if (entityId == null) {
                entityId = extractEntityId(new Object[]{responseBody});
            }

            if (!ActionType.UNKNOWN.equals(actionType)) {
                logger.debug("Building audit event for method: {}", method.getName());

                LocalDateTime timeStamp = extractTimestamp(responseBody, actionType).orElse(LocalDateTime.now());

                AuditEvent event = AuditEvent.builder()
                        .entityName(entityName)
                        .entityId(entityId)
                        .action(actionType.name())
                        .timestamp(timeStamp)
                        .rawDataBefore(rawDataBefore)
                        .rawDataAfter(responseBody)
                        .requestId(UUID.randomUUID().toString())
                        .changedBy("USER")
                        .build();

                logger.debug("Publishing audit event to RabbitMQ, requestId: {}", event.getRequestId());

                try {
                    rabbitTemplate.convertAndSend(auditProperties.getRabbitmq().getExchange(),
                            auditProperties.getRabbitmq().getRoutingKey(), event);
                } catch (Exception e) {
                    logger.warn("Failed to publish audit event to RabbitMQ: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            logger.warn("Audit logging failed silently for method {}: {}", method.getName(), e.getMessage());
        }

        return result;
    }


    private ActionType determineAction(Method method, String entityId) {

        if (method.isAnnotationPresent(DeleteMapping.class) || method.getName().toUpperCase().contains(ActionType.DELETE.name())) {
            return ActionType.DELETE;
        }
        if (method.isAnnotationPresent(PutMapping.class) || method.getName().toUpperCase().contains(ActionType.UPDATE.name())) {
            return ActionType.UPDATE;
        }
        if (method.isAnnotationPresent(PostMapping.class) || method.getName().toUpperCase().contains(ActionType.CREATE.name())) {
            boolean hasEntityId = !Objects.isNull(entityId);

            return hasEntityId ? ActionType.UPDATE : ActionType.CREATE;
        }
        if (method.isAnnotationPresent(Auditable.class)) {
            Auditable auditable = method.getAnnotation(Auditable.class);
            return auditable.actionType();
        }

        return ActionType.UNKNOWN;
    }

    private String extractEntityId(Object[] args) {
        for (Object arg : args) {
            if (arg == null) continue;

            if (arg instanceof Long || arg instanceof String) {
                return String.valueOf(arg);
            }

            try {
                Method getIdMethod = arg.getClass().getMethod("getId");
                Object id = getIdMethod.invoke(arg);
                if (id != null) return String.valueOf(id);
            } catch (Exception e) {
                logger.warn("Error extracting entity ID: {}", e.getMessage());
            }
        }
        return null;
    }

    private String extractEntityName(Object[] args) {
        return Arrays.stream(args)
                .filter(Objects::nonNull)
                .filter(arg -> {
                    String pkg = arg.getClass().getPackageName();
                    return !(pkg.startsWith("java.") || pkg.startsWith("javax.") || pkg.startsWith("org.springframework"));
                })
                .map(arg -> arg.getClass().getSimpleName().replaceAll("(?i)Dto$", ""))
                .findFirst()
                .orElse("UNKNOWN");
    }

    private Object extractResponseBody(Object result) {
        if (result instanceof Optional<?> optional) {
            return optional.orElse(null);
        }

        if (result instanceof ResponseEntity<?> responseEntity) {
            Object body = responseEntity.getBody();
            if (body instanceof ApiResponse<?> apiResponse) {
                return apiResponse.getData();
            }
            return body;
        }
        return result;
    }

    private Object getEntityByNameAndId(String entityName, Object id, EntityManager entityManager) {
        try {
            return entityManager.getMetamodel()
                    .getEntities()
                    .stream()
                    .filter(e -> e.getName().equals(entityName))
                    .findFirst()
                    .map(EntityType::getJavaType)
                    .map(clazz -> entityManager.find(clazz, id))
                    .orElse(null);
        } catch (Exception e) {
            logger.warn("Failed to fetch entity: {}, id: {}, error: {}", entityName, id, e.getMessage());
            return null;
        }
    }

    private Optional<LocalDateTime> extractTimestamp(Object response, ActionType actionType) {
        if (response == null) return Optional.empty();

        try {
            Method timeMethod = switch (actionType) {
                case CREATE -> response.getClass().getMethod("getCreatedAt");
                case UPDATE -> response.getClass().getMethod("getUpdatedAt");
                default -> null;
            };

            if (timeMethod != null) {
                return Optional.ofNullable((LocalDateTime) timeMethod.invoke(response));
            }
        } catch (Exception e) {
            logger.error("Failed to extract time stamp, e = {}", e.getMessage());
        }

        return Optional.empty();
    }

}
