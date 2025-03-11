package org.onelab.gateway_cli_service.aop;

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

    @Pointcut("execution(* org.onelab.gateway_cli_service.service..*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* org.onelab.gateway_cli_service.service..*(..)) || execution(* org.onelab.gateway_cli_service.kafka..*(..))")
    public void serviceAndKafkaMethods() {}

    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Entering method: {} with arguments: {}",
                joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Method {} executed successfully. Result: {}",
                joinPoint.getSignature().toShortString(), result);
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Exception exception) {
        log.error("Exception in method {}: {}", joinPoint.getSignature().toShortString(), exception.getMessage());
    }

    @Around("serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            log.error("Exception during method execution {}: {}", joinPoint.getSignature().toShortString(), ex.getMessage());
            throw ex;
        }
        long duration = System.currentTimeMillis() - start;
        log.info("Method {} executed in {} ms", joinPoint.getSignature().toShortString(), duration);
        return result;
    }

    // Kafka Producer Logging
    @Before("execution(* org.onelab.gateway_cli_service.kafka.KafkaProducer.*(..))")
    public void logKafkaProducerMethods(JoinPoint joinPoint) {
        log.info("KafkaProducer sending message: Method {}, Args: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterThrowing(pointcut = "execution(* org.onelab.gateway_cli_service.kafka.KafkaProducer.*(..))", throwing = "exception")
    public void logKafkaProducerException(JoinPoint joinPoint, Throwable exception) {
        log.error("KafkaProducer Exception in {}: {}", joinPoint.getSignature().toShortString(), exception.getMessage());
    }

    // Kafka Consumer Logging
    @Before("execution(* org.onelab.gateway_cli_service.kafka.KafkaConsumer.*(..))")
    public void logKafkaConsumerMethods(JoinPoint joinPoint) {
        log.info("KafkaConsumer received message in method {} with args: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterThrowing(pointcut = "execution(* org.onelab.gateway_cli_service.kafka.KafkaConsumer.*(..))", throwing = "exception")
    public void logKafkaConsumerException(JoinPoint joinPoint, Throwable exception) {
        log.error("Exception in KafkaConsumer method {}: {}", joinPoint.getSignature().toShortString(), exception.getMessage());
    }
}