package org.onelab.order_worker.service;

import org.onelab.common_lib.dto.OrderDto;

public interface UserService {
    void withdrawBalance(OrderDto order, String businessKey);
    void failedPayment(OrderDto order,String key);
    void successPayment(OrderDto order,String key);
}
