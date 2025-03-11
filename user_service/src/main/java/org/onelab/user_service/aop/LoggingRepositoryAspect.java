package org.onelab.user_service.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingRepositoryAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before("execution(* org.onelab.user_service.repository.*.*(..))")
    public void logRepositoryMethods(JoinPoint joinPoint) {
        logger.info("Repository method called: {} with args {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "execution(* org.onelab.user_service.repository.*.*(..))", returning = "result")
    public void logRepositoryMethodsReturn(JoinPoint joinPoint, Object result) {
        logger.info("Repository method completed: {} returned {}", joinPoint.getSignature().getName(), resultToString(result));
    }

    @AfterThrowing(pointcut = "execution(* org.onelab.user_service.repository.*.*(..))", throwing = "e")
    public void logRepositoryExceptions(JoinPoint joinPoint, Exception e) {
        logger.error("Repository exception in {}: {}", joinPoint.getSignature().getName(), e.getMessage());
    }

    // Kafka Consumer
    @Before("execution(* org.onelab.user_service.kafka.KafkaConsumer.*(..))")
    public void logKafkaConsumerMethods(JoinPoint joinPoint) {
        logger.info("KafkaConsumer method invoked: {} with args {}", joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterThrowing(pointcut = "execution(* org.onelab.user_service.kafka.KafkaConsumer.*.*(..))", throwing = "e")
    public void logKafkaConsumerException(JoinPoint joinPoint, Exception e) {
        logger.error("KafkaConsumer error in method {}: {}", joinPoint.getSignature().getName(), e.getMessage());
    }

    @Before("execution(* org.onelab.user_service.kafka.KafkaProducer.*(..))")
    public void logKafkaProducerMethods(JoinPoint joinPoint) {
        logger.info("KafkaProducer sending message: Method {} args {}", joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterThrowing(pointcut = "execution(* org.onelab.user_service.kafka.KafkaProducer.*(..))", throwing = "e")
    public void logKafkaProducerExceptions(JoinPoint joinPoint, Exception e) {
        logger.error("KafkaProducer method {} failed: {}", joinPoint.getSignature().getName(), e.getMessage());
    }

    private String resultToString(Object result) {
        return result != null ? result.toString() : "null";
    }
}