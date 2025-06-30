package com.recykal.audit.service.strategy;

import org.aspectj.lang.ProceedingJoinPoint;

@FunctionalInterface
public interface AuditStrategy {
    Object execute(ProceedingJoinPoint joinPoint) throws Throwable;
}
