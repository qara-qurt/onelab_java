package org.onelab.gateway_cli_service.shell;

import lombok.RequiredArgsConstructor;
import org.onelab.gateway_cli_service.service.OrderService;
import org.onelab.gateway_cli_service.service.PaymentService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class OrderCLI {

    private final OrderService orderService;
    private final PaymentService paymentService;

    @ShellMethod(key = "create-order", value = "Создать заказ: create-order <customerId> <dishIDs>")
    public String createOrder(@ShellOption String customerId, @ShellOption List<String> dishIDs) {
        return orderService.createOrder(customerId, dishIDs);
    }

    @ShellMethod(key = "get-order", value = "Получить заказ по ID: get-order <orderId>")
    public String getOrder(@ShellOption String orderId) {
        return orderService.getOrder(orderId);
    }

    @ShellMethod(key = "get-orders-by-user", value = "Получить заказы клиента: get-orders-by-user <userId>")
    public String getOrdersByUser(@ShellOption String userId, @ShellOption(defaultValue = "1") int page, @ShellOption(defaultValue = "10") int size) {
        return orderService.getOrdersByUser(userId, page, size);
    }

    @ShellMethod(key = "get-orders", value = "Получить все заказы: get-orders")
    public String getOrders(@ShellOption(defaultValue = "1") int page, @ShellOption(defaultValue = "10") int size) {
        return orderService.getOrders(page, size);
    }

    @ShellMethod(key = "failed-payments", value = "Посмотреть неудачные платежи")
    public String getFailedPayments() {
        return paymentService.getFailedPayments();
    }

    @ShellMethod(key = "successful-payments", value = "Посмотреть успешные платежи")
    public String getSuccessfulPayments() {
        return paymentService.getSuccessfulPayments();
    }
}
