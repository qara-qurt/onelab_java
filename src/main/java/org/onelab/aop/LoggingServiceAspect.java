package org.onelab.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
public class LoggingServiceAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingServiceAspect.class.getName());

    @Pointcut("execution(* org.onelab.service.*.*(..))")
    public void serviceMethods() {}

    @Before("serviceMethods()")
    public void logBefore(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        log.info("Calling service method: {} with arguments: {}", signature.toShortString(), Arrays.toString(args));
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(ProceedingJoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.info("Service method {} executed successfully. Result: {}", signature.toShortString(), result);
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logAfterThrowing(ProceedingJoinPoint joinPoint, Exception ex) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        log.error("Error in service method {}: {}", signature.toShortString(), ex.getMessage(), ex);
    }

    @Around("serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable ex) {
            log.error("Exception during execution of service method {}: {}", joinPoint.getSignature(), ex.getMessage(), ex);
            throw ex;
        }
        long duration = System.currentTimeMillis() - start;
        log.info("Service method {} executed in {} ms", joinPoint.getSignature(), duration);
        return result;
    }
}
