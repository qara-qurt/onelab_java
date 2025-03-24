package org.onelab.order_worker.service;

import org.onelab.common_lib.dto.OrderDto;

import java.util.List;

public interface OrderService {
    OrderDto createOrder(Long userId, List<Long> dishIds);
}
