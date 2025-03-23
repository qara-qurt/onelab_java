package org.onelab.camunda_service.delegate;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.onelab.camunda_service.client.RestaurantClient;
import org.onelab.camunda_service.config.FeignConfig;
import org.onelab.camunda_service.dto.OrderDto;
import org.onelab.camunda_service.dto.OrderRequestDto;
import org.onelab.camunda_service.service.OrderService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("createOrderDelegate")
@RequiredArgsConstructor
public class CreateOrderDelegate implements JavaDelegate {

    private final OrderService orderService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long userId = (Long) execution.getVariable("userId");
        List<?> rawList = (List<?>) execution.getVariable("dishIds");
        List<Long> dishIds = rawList.stream()
                .map(id -> ((Number) id).longValue())
                .toList();
        String token = (String) execution.getVariable("bearerToken");

        FeignConfig.setToken(token);

        Thread.sleep(5000);

        OrderDto order = orderService.createOrder(OrderRequestDto.builder()
                .customerId(userId)
                .dishIds(dishIds)
                .build());


        execution.setVariable("orderCreated", true);
        execution.setVariable("order", order);
    }
}
