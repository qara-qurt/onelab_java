package org.onelab.restaurant_service.service;

import java.util.List;

public interface OrderService {
    String createOrder(String userId, List<String> dishIDs);
}
