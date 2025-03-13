package org.onelab.gateway_cli_service.service;

import lombok.RequiredArgsConstructor;
import org.onelab.gateway_cli_service.client.RestaurantClient;
import org.onelab.gateway_cli_service.client.TokenStorage;
import org.onelab.gateway_cli_service.dto.DishDto;
import org.onelab.gateway_cli_service.dto.OrderDto;
import org.onelab.gateway_cli_service.dto.OrderRequestDto;
import org.onelab.gateway_cli_service.kafka.KafkaProducer;
import org.onelab.gateway_cli_service.utils.JwtToken;
import org.onelab.gateway_cli_service.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final RestaurantClient restaurantClient;
    private final KafkaProducer kafkaProducer;
    private final TokenStorage tokenStorage;
    private final JwtToken jwtTokenUtil;

    @Override
    public String createOrder(Long customerID,List<Long> dishIDs) {
        try {

            List<DishDto> foundDishes = restaurantClient.getDishes(1, 100);
            List<Long> missingDishes = dishIDs.stream()
                    .filter(id -> foundDishes.stream().noneMatch(dish -> dish.getId().equals(id)))
                    .toList();

            if (!missingDishes.isEmpty()) {
                return "❌ Некоторые блюда не найдены: " + missingDishes;
            }

            OrderRequestDto orderRequest = new OrderRequestDto(customerID, dishIDs);
            OrderDto order = restaurantClient.createOrder(orderRequest);

            return "✅ Заказ #" + order.getId() + " для клиента " + customerID + " создан.";
        } catch (Exception e) {
            return "❌ Ошибка при создании заказа: " + e.getMessage();
        }
    }

    @Override
    public String getOrder(Long orderId) {
        try {
            OrderDto order = restaurantClient.getOrder(orderId);
            return Utils.formatOrder(order);
        } catch (Exception e) {
            return "❌ Заказ с ID " + orderId + " не найден.";
        }
    }

    @Override
    public String getOrdersByUser(int page, int size) {
        try {
            String token = tokenStorage.getToken();
            if (token == null || token.isEmpty()) {
                return "❌ Error: No authentication token found.";
            }

            Long userId = jwtTokenUtil.getUserIdFromToken(token);
            if (userId == null) {
                return "❌ Error: Could not extract userId from token.";
            }

            List<OrderDto> orders = restaurantClient.getOrdersByUser(userId, page, size);
            if (orders.isEmpty()) {
                return "❌ No orders found for user ID: " + userId;
            }

            return orders.stream()
                    .map(Utils::formatOrder)
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            return "❌ Error retrieving orders: " + e.getMessage();
        }
    }

    @Override
    public String getOrders(int page, int size) {
        try {
            List<OrderDto> orders = restaurantClient.getOrders(page, size);
            if (orders.isEmpty()) {
                return "❌ Заказы отсутствуют.";
            }
            return orders.stream()
                    .map(Utils::formatOrder)
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            return "❌ Ошибка при получении заказов.";
        }
    }
}
