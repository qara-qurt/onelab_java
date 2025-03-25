package org.onelab.order_worker.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.order_worker.service.OrderService;
import org.onelab.order_worker.service.UserService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderHandlerTest {

    private OrderService orderService;
    private UserService userService;
    private ObjectMapper objectMapper;
    private OrderHandler orderHandler;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        userService = mock(UserService.class);
        objectMapper = new ObjectMapper();
        orderHandler = new OrderHandler(orderService, userService, objectMapper);
    }

    @Test
    void testCreateOrderHandler_success() throws Exception {
        ExternalTask task = mock(ExternalTask.class);
        ExternalTaskService taskService = mock(ExternalTaskService.class);

        when(task.getVariable("userId")).thenReturn(1L);
        when(task.getVariable("dishIds")).thenReturn(List.of(10, 20));
        when(task.getVariable("bearerToken")).thenReturn("mock-token");

        OrderDto mockOrder = OrderDto.builder()
                .id(123L)
                .totalPrice(150.0)
                .build();

        when(orderService.createOrder(1L, List.of(10L, 20L))).thenReturn(mockOrder);

        orderHandler.createOrderHandler().execute(task, taskService);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(taskService).complete(eq(task), captor.capture());

        Map<String, Object> variables = captor.getValue();
        assertEquals(123L, variables.get("orderId"));
        assertEquals(150.0, variables.get("totalPrice"));
        assertTrue(variables.get("orderJson") instanceof ObjectValue);
    }

    @Test
    void testWithdrawBalanceHandler_success() throws Exception {
        ExternalTask task = mock(ExternalTask.class);
        ExternalTaskService taskService = mock(ExternalTaskService.class);

        OrderDto mockOrder = OrderDto.builder().id(42L).build();
        String json = objectMapper.writeValueAsString(mockOrder);

        when(task.getBusinessKey()).thenReturn("ORDER-42");
        when(task.getVariable("orderJson")).thenReturn(json);

        orderHandler.withdrawBalanceHandler().execute(task, taskService);

        verify(userService).withdrawBalance(eq(mockOrder), eq("ORDER-42"));
        verify(taskService).complete(task);
    }

    @Test
    void testNotifyOrderHandler_logsAndCompletes() throws Exception {
        ExternalTask task = mock(ExternalTask.class);
        ExternalTaskService taskService = mock(ExternalTaskService.class);

        when(task.getVariable("paymentSuccess")).thenReturn(true);
        when(task.getVariable("orderId")).thenReturn(555L);

        orderHandler.notifyOrderHandler().execute(task, taskService);

        verify(taskService).complete(task);
    }
}
