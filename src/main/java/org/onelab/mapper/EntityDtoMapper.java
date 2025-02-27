package org.onelab.mapper;

import org.onelab.dto.DishDto;
import org.onelab.dto.OrderDto;
import org.onelab.dto.UserDto;
import org.onelab.entity.Dish;
import org.onelab.entity.Order;
import org.onelab.entity.User;

import java.util.stream.Collectors;

public class EntityDtoMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .phone(userDto.getPhone())
                .build();
    }

    public static DishDto toDishDto(Dish dish) {
        return DishDto.builder()
                .id(dish.getId())
                .name(dish.getName())
                .price(dish.getPrice())
                .build();
    }

    public static Dish toDish(DishDto dishDto) {
        return Dish.builder()
                .id(dishDto.getId())
                .name(dishDto.getName())
                .price(dishDto.getPrice())
                .build();
    }

    public static OrderDto toOrderDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .customer(toUserDto(order.getCustomer()))
                .dishes(order.getDishes().stream().map(EntityDtoMapper::toDishDto).collect(Collectors.toList()))
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .build();
    }

    public static Order toOrder(OrderDto orderDto) {
        return Order.builder()
                .id(orderDto.getId())
                .customer(toUser(orderDto.getCustomer()))
                .dishes(orderDto.getDishes().stream().map(EntityDtoMapper::toDish).collect(Collectors.toList()))
                .status(orderDto.getStatus())
                .totalPrice(orderDto.getTotalPrice())
                .build();
    }
}
