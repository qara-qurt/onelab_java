package org.onelab.gateway_cli_service.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingClientAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingServiceAspect.class);

    @Around("execution(* org.onelab.gateway_cli_service.client.*.*(..))")
    public Object logFeignRequests(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        log.info("Feign Client Request: {}.{}() | Args: {}", className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - startTime;
            log.info("Feign Client Response: {}.{}() | Result: {} | Time: {}ms", className, methodName, result, elapsedTime);
            return result;
        } catch (Exception e) {
            log.error("Feign Client Error: {}.{}() | Exception: {}", className, methodName, e.getMessage());
            throw e;
        }
    }
}
