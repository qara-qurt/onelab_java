package org.onelab.restaurant_service.mapper;

import org.onelab.restaurant_service.dto.OrderDto;
import org.onelab.restaurant_service.entity.OrderDocument;
import org.onelab.restaurant_service.entity.OrderEntity;

public class OrderMapper {
    public static OrderDto toDto(OrderEntity order) {
        return OrderDto.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .dishes(order.getDishes().stream()
                        .map(DishMapper::toDto)
                        .toList())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .build();
    }

    public static OrderDto toDto(OrderDocument order) {
        return OrderDto.builder()
                .id(order.getId() != null ? Long.valueOf(order.getId()) : null)
                .customerId(Long.valueOf(order.getCustomerId()))
                .dishes(order.getDishes().stream()
                        .map(DishMapper::toDto)
                        .toList())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .build();
    }

    public static OrderEntity toEntity(OrderDto dto) {
        return OrderEntity.builder()
                .id(dto.getId())
                .customerId(dto.getCustomerId())
                .dishes(dto.getDishes().stream()
                        .map(DishMapper::toEntity)
                        .toList())
                .status(dto.getStatus())
                .totalPrice(dto.getTotalPrice())
                .build();
    }

    public static OrderDocument toDocument(OrderEntity order) {
        return OrderDocument.builder()
                .id(order.getId() != null ? String.valueOf(order.getId()) : null)
                .customerId(String.valueOf(order.getCustomerId()))
                .dishes(order.getDishes().stream()
                        .map(DishMapper::toDocument)
                        .toList())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .build();
    }
}
