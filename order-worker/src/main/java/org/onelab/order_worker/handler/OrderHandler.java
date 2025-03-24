package org.onelab.order_worker.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.order_worker.config.FeignTokenInterceptor;
import org.onelab.order_worker.service.OrderService;
import org.onelab.order_worker.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OrderHandler {

    private final OrderService orderService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Bean
    @ExternalTaskSubscription("create-order")
    public ExternalTaskHandler createOrderHandler () throws InterruptedException {
        Thread.sleep(3000);
        return (externalTask, externalTaskService) -> {
            Long userId = externalTask.getVariable("userId");
            List<?> rawDishIds = externalTask.getVariable("dishIds");
            List<Long> dishIds = rawDishIds.stream()
                    .map(d -> ((Number) d).longValue())
                    .toList();

            String token = externalTask.getVariable("bearerToken");

            try {

                FeignTokenInterceptor.setToken(token);
                OrderDto order = orderService.createOrder(userId, dishIds);

                ObjectValue orderValue = Variables
                        .objectValue(objectMapper.writeValueAsString(order))
                        .serializationDataFormat("application/json")
                        .create();

                Map<String, Object> variables = new HashMap<>();
                variables.put("orderId", order.getId());
                variables.put("totalPrice", order.getTotalPrice());
                variables.put("orderJson", orderValue);

                externalTaskService.complete(externalTask, variables);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } finally {
                FeignTokenInterceptor.clearToken();
            }
        };
    }



    @Bean
    @ExternalTaskSubscription("withdraw-balance")
    public ExternalTaskHandler withdrawBalanceHandler() throws InterruptedException {
        Thread.sleep(3000);
        return (externalTask, externalTaskService) -> {
            String businessKey = externalTask.getBusinessKey();
            String orderJson = externalTask.getVariable("orderJson");

            OrderDto order = null;
            try {
                order = objectMapper.readValue(orderJson, OrderDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            userService.withdrawBalance(order, businessKey);

            externalTaskService.complete(externalTask);
        };
    }


    @Bean
    @ExternalTaskSubscription("notify-order")
    public ExternalTaskHandler notifyOrderHandler() throws InterruptedException {
        Thread.sleep(3000);
        return (externalTask, externalTaskService) -> {
            boolean paymentSuccess = externalTask.getVariable("paymentSuccess");
            Long orderId = externalTask.getVariable("orderId");

            log.info("orderId: {}, paymentSuccess: {}", orderId, paymentSuccess);

            externalTaskService.complete(externalTask);
        };
    }

}
