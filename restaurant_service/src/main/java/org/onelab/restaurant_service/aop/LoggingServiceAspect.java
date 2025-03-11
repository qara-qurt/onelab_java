package org.onelab.restaurant_service.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingServiceAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* org.onelab.restaurant_service.service.*.*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* org.onelab.restaurant_service.kafka.*.*(..))")
    public void kafkaMethods() {}


    @Before("serviceMethods()")
    public void logServiceBefore(JoinPoint joinPoint) {
        log.info("[SERVICE BEFORE] {}.{}(), args: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logServiceAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("[SERVICE AFTER] {}.{}(), returned: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                result);
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logServiceException(JoinPoint joinPoint, Throwable ex) {
        log.error("[SERVICE EXCEPTION] {}.{}(), error: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                ex.getMessage());
    }

    @Before("kafkaMethods()")
    public void logKafkaBefore(JoinPoint joinPoint) {
        log.info("[KAFKA BEFORE] {}.{}(), args: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning("kafkaMethods()")
    public void logKafkaAfter(JoinPoint joinPoint) {
        log.info("[KAFKA AFTER] {}.{}(), executed successfully",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "kafkaMethods()", throwing = "ex")
    public void logKafkaException(JoinPoint joinPoint, Throwable ex) {
        log.error("[KAFKA EXCEPTION] {}.{}(), error: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                ex.getMessage());
    }
}
