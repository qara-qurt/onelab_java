package org.onelab.user_service.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingControllerAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Around("execution(* org.onelab.user_service.controller.*.*(..))")
    public Object logControllerRequests(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        log.info("HTTP Request: {}.{}() | Args: {}", className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - startTime;

            if (result instanceof ResponseEntity<?> responseEntity) {
                log.info("HTTP Response: {}.{}() | Status: {} | Time: {}ms",
                        className, methodName, responseEntity.getStatusCode(), elapsedTime);
            } else {
                log.info("HTTP Response: {}.{}() | Result: {} | Time: {}ms",
                        className, methodName, result, elapsedTime);
            }

            return result;
        } catch (Exception e) {
            log.error("HTTP Error: {}.{}() | Exception: {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}
