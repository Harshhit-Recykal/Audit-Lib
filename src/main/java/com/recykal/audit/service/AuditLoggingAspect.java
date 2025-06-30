package com.recykal.audit.service;

import com.recykal.audit.service.strategy.AuditableAnnotationAuditStrategy;
import com.recykal.audit.service.strategy.RequestMappingAuditStrategy;
import com.recykal.audit.service.strategy.ServiceClassAuditStrategy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditLoggingAspect {

    private final RequestMappingAuditStrategy requestMappingStrategy;
    private final ServiceClassAuditStrategy serviceStrategy;
    private final AuditableAnnotationAuditStrategy annotationStrategy;

    public AuditLoggingAspect(RequestMappingAuditStrategy requestMappingStrategy,
                       ServiceClassAuditStrategy serviceStrategy,
                       AuditableAnnotationAuditStrategy annotationStrategy) {
        this.requestMappingStrategy = requestMappingStrategy;
        this.serviceStrategy = serviceStrategy;
        this.annotationStrategy = annotationStrategy;
    }

    @Around("com.recykal.audit.utils.PointcutUtils.logAroundBasedOnRequestMapping()")
    public Object aroundRequestMapping(ProceedingJoinPoint joinPoint) throws Throwable {
        return requestMappingStrategy.execute(joinPoint);
    }

    @Around("com.recykal.audit.utils.PointcutUtils.logAroundBasedOnServiceClass()")
    public Object aroundServiceClass(ProceedingJoinPoint joinPoint) throws Throwable {
        return serviceStrategy.execute(joinPoint);
    }

    @Around("com.recykal.audit.utils.PointcutUtils.logAroundBasedOnAuditableAnnotation()")
    public Object aroundAuditableAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        return annotationStrategy.execute(joinPoint);
    }
}