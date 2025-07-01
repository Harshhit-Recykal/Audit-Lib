package com.recykal.audit.service.strategy;

import com.recykal.audit.dto.AuditProperties;
import com.recykal.audit.enums.AuditPointcutType;
import com.recykal.audit.service.AuditLoggingHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RequestMappingAuditStrategy implements AuditStrategy {

    private final AuditLoggingHandler auditLoggingHandler;

    private final AuditProperties auditProperties;

    @Autowired
    public RequestMappingAuditStrategy(AuditLoggingHandler auditLoggingHandler, AuditProperties auditProperties) {
        this.auditLoggingHandler = auditLoggingHandler;
        this.auditProperties = auditProperties;
    }

    @Override
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        if (auditProperties.getPointcutType() != AuditPointcutType.REQUEST_MAPPING) {
            return joinPoint.proceed();
        }
        return auditLoggingHandler.handle(joinPoint);
    }
}
