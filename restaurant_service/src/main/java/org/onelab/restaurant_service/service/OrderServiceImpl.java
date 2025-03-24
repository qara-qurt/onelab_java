package org.onelab.restaurant_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.onelab.common_lib.dto.OrderDto;
import org.onelab.common_lib.enums.OrderStatus;
import org.onelab.restaurant_service.entity.*;
import org.onelab.restaurant_service.exception.NotFoundException;
import org.onelab.restaurant_service.mapper.OrderMapper;
import org.onelab.restaurant_service.repository.DishRepository;
import org.onelab.restaurant_service.repository.OrderElasticRepository;
import org.onelab.restaurant_service.repository.OrderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final OrderElasticRepository orderElasticRepository;

    @Override
    @Transactional
    public OrderDto createOrder(Long userId, List<Long> dishIds) {
        List<DishEntity> foundDishes = dishRepository.findAllById(dishIds);

        if (foundDishes.size() != dishIds.size()) {
            throw new NotFoundException("Some dishes not found.");
        }

        double totalPrice = foundDishes.stream().mapToDouble(DishEntity::getPrice).sum();

        OrderEntity order = OrderEntity.builder()
                .customerId(userId)
                .dishes(foundDishes)
                .status(OrderStatus.NEW)
                .totalPrice(totalPrice)
                .build();

        OrderEntity savedOrder = orderRepository.save(order);

        syncOrderToElastic(savedOrder);

        return OrderMapper.toDto(savedOrder);
    }

    @Override
    public OrderDto getOrder(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found."));
        return OrderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getOrdersByUser(Long userId, int page, int size) {
        return orderRepository.findByCustomerId(userId, PageRequest.of(page - 1, size))
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderDto> getOrders(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page - 1, size))
                .stream()
                .map(OrderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto updateOrder(OrderDto orderDto) {
        OrderEntity order = orderRepository.findById(orderDto.getId())
                .orElseThrow(() -> new NotFoundException("Order not found."));

        order.setStatus(orderDto.getStatus());
        OrderEntity updatedOrder = orderRepository.save(order);

        syncOrderToElastic(updatedOrder);

        return OrderMapper.toDto(updatedOrder);
    }

    private void syncOrderToElastic(OrderEntity order) {
        OrderDocument orderDocument = OrderMapper.toDocument(order);
        orderElasticRepository.save(orderDocument);
    }
}
