
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
class LoggingServiceAspectTest {

    @InjectMocks
    private LoggingServiceAspect loggingServiceAspect;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @BeforeEach
    void setUp() {
        loggingServiceAspect = new LoggingServiceAspect();
    }

    @Test
    void testLogBeforeServiceMethod() {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.toShortString()).thenReturn("testServiceMethod()");
        when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", 2});

        loggingServiceAspect.logBefore(joinPoint);

        verify(joinPoint, times(1)).getSignature();
        verify(joinPoint, times(1)).getArgs();
    }

    @Test
    void testLogAfterReturningServiceMethod() {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.toShortString()).thenReturn("testServiceMethod()");

        loggingServiceAspect.logAfterReturning(joinPoint, "result");

        verify(joinPoint, times(1)).getSignature();
    }

    @Test
    void testLogAfterThrowingServiceMethod() {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.toShortString()).thenReturn("testServiceMethod()");
        Exception ex = new Exception("Test exception");

        loggingServiceAspect.logAfterThrowing(joinPoint, ex);

        verify(joinPoint, times(1)).getSignature();
    }

    @Test
    void testLogExecutionTimeServiceMethod() throws Throwable {
        when(joinPoint.proceed()).thenReturn("result");

        Object result = loggingServiceAspect.logExecutionTime(joinPoint);

        verify(joinPoint, times(1)).proceed();
        assertEquals("result", result);
    }

}
