package org.onelab.user_service.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingServiceAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingServiceAspect.class);

    @Pointcut("execution(* org.onelab.user_service.service..*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* org.onelab.user_service.kafka..*(..))")
    public void kafkaMethods() {}

    @Before("serviceMethods() || kafkaMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Entering method: {} with arguments: {}",
                joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(value = "serviceMethods() || kafkaProducerMethods() || kafkaConsumerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Method {} executed successfully. Result: {}",
                joinPoint.getSignature().toShortString(), result);
    }

    @AfterThrowing(pointcut = "serviceMethods() || kafkaProducerMethods() || kafkaConsumerMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("Exception in method {}: {}",
                joinPoint.getSignature().toShortString(), exception.getMessage(), exception);
    }

    @Around("serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        log.info("Method {} executed in {} ms",
                joinPoint.getSignature().toShortString(), duration);
        return result;
    }

    @Pointcut("execution(* org.onelab.user_service.kafka.KafkaProducer.*(..))")
    public void kafkaProducerMethods() {}

    @Pointcut("execution(* org.onelab.user_service.kafka.KafkaConsumer.*(..))")
    public void kafkaConsumerMethods() {}

}
