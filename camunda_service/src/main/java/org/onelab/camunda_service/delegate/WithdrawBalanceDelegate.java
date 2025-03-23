package org.onelab.camunda_service.delegate;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.onelab.camunda_service.dto.OrderDto;
import org.onelab.camunda_service.service.OrderService;
import org.onelab.camunda_service.service.UserService;
import org.springframework.stereotype.Component;

@Component("withdrawBalanceDelegate")
@RequiredArgsConstructor
public class WithdrawBalanceDelegate implements JavaDelegate {

    private final UserService userService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        OrderDto order = (OrderDto) execution.getVariable("order");
        String businessKey = execution.getProcessBusinessKey();
        Thread.sleep(5000);
        userService.withdrawBalance(order,businessKey);
    }
}
