package org.onelab.gateway_cli_service.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
public class LoggingRepositoryAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingRepositoryAspect.class);

    @Pointcut("execution(* org.onelab.gateway_cli_service.repository..*(..))")
    public void repositoryMethods() {}

    @Before("repositoryMethods()")
    public void logBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        log.info("Calling repository method: {} with arguments: {}", signature.toShortString(), Arrays.toString(args));
    }

    @AfterReturning(pointcut = "repositoryMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("Repository method {} executed successfully. Result: {}", signature.toShortString(), result);
    }

    @AfterThrowing(pointcut = "repositoryMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Exception ex) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.error("Error in repository method {}: {}", signature.toShortString(), ex.getMessage(), ex);
    }

    @Around("repositoryMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            log.error("Exception during execution of repository method {}: {}", joinPoint.getSignature(), ex.getMessage(), ex);
            throw ex;
        }
        long duration = System.currentTimeMillis() - start;
        log.info("Repository method {} executed in {} ms", joinPoint.getSignature(), duration);
        return result;
    }
}
