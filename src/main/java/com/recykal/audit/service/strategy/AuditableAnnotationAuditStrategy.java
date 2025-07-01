package com.recykal.audit.service.strategy;

import com.recykal.audit.dto.AuditProperties;
import com.recykal.audit.enums.AuditPointcutType;
import com.recykal.audit.service.AuditLoggingHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditableAnnotationAuditStrategy implements AuditStrategy {

    private final AuditLoggingHandler auditLoggingHandler;

    private final AuditProperties auditProperties;

    @Autowired
    public AuditableAnnotationAuditStrategy(AuditLoggingHandler auditLoggingHandler, AuditProperties auditProperties) {
        this.auditLoggingHandler = auditLoggingHandler;
        this.auditProperties = auditProperties;
    }

    @Override
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        if (auditProperties.getPointcutType() != AuditPointcutType.AUDITABLE_ANNOTATION) {
            return joinPoint.proceed();
        }
        return auditLoggingHandler.handle(joinPoint);
    }
}
