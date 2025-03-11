package org.onelab.gateway_cli_service.service;

import java.util.List;

public interface OrderService {
    String createOrder(String customerId, List<String> dishIDs);
    String getOrder(String orderId);
    String getOrdersByUser(String userId, int page, int size);
    String getOrders(int page, int size);
}
