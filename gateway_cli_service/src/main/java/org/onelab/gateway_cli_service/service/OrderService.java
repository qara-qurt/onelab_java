package org.onelab.gateway_cli_service.service;

import java.util.List;

public interface OrderService {
    String createOrder(Long customerID,List<Long> dishIDs);
    String getOrder(Long orderId);
    String getOrdersByUser(int page, int size);
    String getOrders(int page, int size);
}
