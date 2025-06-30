package com.recykal.audit.service.strategy;

import com.recykal.audit.dto.AuditProperties;
import com.recykal.audit.enums.AuditPointcutType;
import com.recykal.audit.service.AuditLoggingHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceClassAuditStrategy implements AuditStrategy {

    private final AuditLoggingHandler auditLoggingHandler;

    private final AuditProperties auditProperties;

    @Autowired
    public ServiceClassAuditStrategy(AuditLoggingHandler auditLoggingHandler, AuditProperties auditProperties) {
        this.auditLoggingHandler = auditLoggingHandler;
        this.auditProperties = auditProperties;
    }

    @Override
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        if (auditProperties.getAuditPointcutType() != AuditPointcutType.SERVICE_CLASS) {
            return joinPoint.proceed();
        }
        return auditLoggingHandler.handle(joinPoint);
    }
}
