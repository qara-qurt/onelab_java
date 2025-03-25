package org.onelab.order_worker.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class KafkaLoggingAspect {

    private final Logger log = LoggerFactory.getLogger(KafkaLoggingAspect.class);

    @Pointcut("@annotation(kafkaListener)")
    public void kafkaListenerMethods(KafkaListener kafkaListener) {}

    @Pointcut("execution(* org.onelab.order_worker.kafka.KafkaProducer.*(..))")
    public void kafkaProducerMethods() {}

    @Before("kafkaListenerMethods(kafkaListener)")
    public void logBeforeKafkaConsumer(JoinPoint joinPoint, KafkaListener kafkaListener) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        log.info("[KafkaConsumer] Consuming from topics: {} | Method: {} | Args: {}",
                Arrays.toString(kafkaListener.topics()),
                signature.getName(),
                Arrays.toString(args));
    }

    @AfterReturning(pointcut = "kafkaListenerMethods(kafkaListener)", returning = "result")
    public void logAfterKafkaConsumer(KafkaListener kafkaListener, JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("[KafkaConsumer] Completed method: {} from topics: {}",
                signature.getName(),
                Arrays.toString(kafkaListener.topics()));
    }

    @AfterThrowing(pointcut = "kafkaListenerMethods(kafkaListener)", throwing = "ex")
    public void logKafkaConsumerException(KafkaListener kafkaListener, JoinPoint joinPoint, Throwable ex) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.error("[KafkaConsumer] Error in method: {} | Topic: {} | Message: {}",
                signature.getName(),
                Arrays.toString(kafkaListener.topics()),
                ex.getMessage(), ex);
    }

    @Before("kafkaProducerMethods()")
    public void logBeforeKafkaProducer(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("[KafkaProducer] Sending message from method: {} | Args: {}",
                signature.getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning("kafkaProducerMethods()")
    public void logAfterKafkaProducer(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("[KafkaProducer] Successfully sent from method: {}", signature.getName());
    }

    @AfterThrowing(pointcut = "kafkaProducerMethods()", throwing = "ex")
    public void logKafkaProducerException(JoinPoint joinPoint, Throwable ex) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.error("[KafkaProducer] Failed to send message in method: {} | Error: {}",
                signature.getName(),
                ex.getMessage(), ex);
    }
}
