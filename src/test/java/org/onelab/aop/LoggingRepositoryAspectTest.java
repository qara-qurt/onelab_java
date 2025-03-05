package org.onelab.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingRepositoryAspectTest {

    @InjectMocks
    private LoggingRepositoryAspect loggingRepositoryAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @BeforeEach
    void setUp() {
        loggingRepositoryAspect = new LoggingRepositoryAspect();
    }

    @Test
    void testLogBeforeRepositoryMethod() {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.toShortString()).thenReturn("testRepositoryMethod()");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", 2});

        loggingRepositoryAspect.logBefore(joinPoint);

        verify(joinPoint, times(1)).getSignature();
        verify(joinPoint, times(1)).getArgs();
    }

    @Test
    void testLogAfterReturningRepositoryMethod() {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.toShortString()).thenReturn("testRepositoryMethod()");

        loggingRepositoryAspect.logAfterReturning(joinPoint, "result");

        verify(joinPoint, times(1)).getSignature();
    }

    @Test
    void testLogAfterThrowingRepositoryMethod() {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.toShortString()).thenReturn("testRepositoryMethod()");
        Exception ex = new Exception("Test exception");

        loggingRepositoryAspect.logAfterThrowing(joinPoint, ex);

        verify(joinPoint, times(1)).getSignature();
    }

    @Test
    void testLogExecutionTimeRepositoryMethod() throws Throwable {
        when(joinPoint.proceed()).thenReturn("result");

        Object result = loggingRepositoryAspect.logExecutionTime(joinPoint);

        verify(joinPoint, times(1)).proceed();
        assertEquals("result", result);
    }

}
