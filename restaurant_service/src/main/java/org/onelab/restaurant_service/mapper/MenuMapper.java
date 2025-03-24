package org.onelab.restaurant_service.mapper;

import org.onelab.common_lib.dto.MenuDto;
import org.onelab.restaurant_service.entity.MenuDocument;
import org.onelab.restaurant_service.entity.MenuEntity;

import java.util.stream.Collectors;

public class MenuMapper {

    public static MenuDto toDto(MenuEntity menu) {
        return MenuDto.builder()
                .id(menu.getId())
                .name(menu.getName())
                .dishes(menu.getDishes().stream()
                        .map(DishMapper::toDto)
                        .toList())
                .build();
    }

    public static MenuDto toDto(MenuDocument menu) {
        return MenuDto.builder()
                .id(menu.getId() != null ? Long.valueOf(menu.getId()) : null) // Преобразование String -> Long
                .name(menu.getName())
                .dishes(menu.getDishes().stream()
                        .map(DishMapper::toDto)
                        .toList())
                .build();
    }

    public static MenuEntity toEntity(MenuDto dto) {
        return MenuEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .dishes(dto.getDishes().stream()
                        .map(DishMapper::toEntity)
                        .toList())
                .build();
    }

    public static MenuDocument toDocument(MenuEntity menu) {
        return MenuDocument.builder()
                .id(menu.getId() != null ? String.valueOf(menu.getId()) : null) // Преобразование Long -> String
                .name(menu.getName())
                .dishes(menu.getDishes().stream()
                        .map(DishMapper::toDocument)
                        .toList())
                .build();
    }
}
