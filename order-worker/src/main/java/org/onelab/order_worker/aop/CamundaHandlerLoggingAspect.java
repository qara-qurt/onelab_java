package org.onelab.order_worker.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.camunda.bpm.client.task.ExternalTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CamundaHandlerLoggingAspect {

    private final Logger log = LoggerFactory.getLogger(CamundaHandlerLoggingAspect.class);

    @Pointcut("execution(org.camunda.bpm.client.task.ExternalTaskHandler org.onelab.order_worker.handler..*Handler(..))")
    public void externalTaskHandlerFactoryMethods() {}

    @Before("externalTaskHandlerFactoryMethods()")
    public void logHandlerCreation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("Creating handler bean: {}", signature.getName());
    }

    @Pointcut("execution(* org.camunda.bpm.client.task.ExternalTaskHandler.execute(..))")
    public void externalTaskExecuteMethod() {}

    @Before("externalTaskExecuteMethod()")
    public void logBeforeHandlerExecution(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length >= 1 && args[0] instanceof ExternalTask task) {
            log.info("[Camunda] Executing task (topic={} | businessKey={}) with variables: {}",
                    task.getTopicName(), task.getBusinessKey(), task.getAllVariables());
        }
    }

    @After("externalTaskExecuteMethod()")
    public void logAfterHandlerExecution(JoinPoint joinPoint) {
        log.info("[Camunda] Task handler completed: {}", joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "externalTaskExecuteMethod()", throwing = "ex")
    public void logHandlerException(JoinPoint joinPoint, Throwable ex) {
        log.error("[Camunda] Task handler threw exception: {} | Message: {}",
                joinPoint.getSignature().getName(), ex.getMessage(), ex);
    }
}
