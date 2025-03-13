package org.onelab.restaurant_service.mapper;

import org.onelab.restaurant_service.dto.DishDto;
import org.onelab.restaurant_service.entity.DishDocument;
import org.onelab.restaurant_service.entity.DishEntity;

public class DishMapper {

    public static DishDto toDto(DishEntity dish) {
        return DishDto.builder()
                .id(dish.getId())
                .name(dish.getName())
                .price(dish.getPrice())
                .description(dish.getDescription())
                .createdAt(dish.getCreatedAt())
                .updatedAt(dish.getUpdatedAt())
                .build();
    }

    public static DishDto toDto(DishDocument dish) {
        return DishDto.builder()
                .id(dish.getId() != null ? Long.valueOf(dish.getId()) : null)
                .name(dish.getName())
                .price(dish.getPrice())
                .description(dish.getDescription())
                .createdAt(dish.getCreatedAt())
                .updatedAt(dish.getUpdatedAt())
                .build();
    }

    public static DishEntity toEntity(DishDto dto) {
        return DishEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public static DishDocument toDocument(DishEntity dish) {
        return DishDocument.builder()
                .id(dish.getId() != null ? String.valueOf(dish.getId()) : null)
                .name(dish.getName())
                .price(dish.getPrice())
                .description(dish.getDescription())
                .createdAt(dish.getCreatedAt())
                .updatedAt(dish.getUpdatedAt())
                .build();
    }
}
