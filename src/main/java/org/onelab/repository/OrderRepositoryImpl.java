package org.onelab.repository;

import org.onelab.entity.Order;
import org.onelab.entity.OrderStatus;
import org.onelab.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private final JdbcTemplate jdbcTemplate;

    public OrderRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Order order) {
        String sql = "INSERT INTO orders (customer_id, status, total_price) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql,
                order.getCustomer().getId(),
                order.getStatus().name(),
                order.getTotalPrice()
        );
    }


    @Override
    public Order findById(Long id) {
        String sql = "SELECT o.id, o.status, o.total_price, u.id as user_id, u.name, u.phone " +
                "FROM orders o JOIN users u ON o.customer_id = u.id WHERE o.id = ?";
        return jdbcTemplate.queryForObject(sql, orderRowMapper, id);
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT o.id, o.status, o.total_price, u.id as user_id, u.name, u.phone " +
                "FROM orders o JOIN users u ON o.customer_id = u.id";
        return jdbcTemplate.query(sql, orderRowMapper);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        String sql = "SELECT o.id, o.status, o.total_price, u.id as user_id, u.name, u.phone " +
                "FROM orders o JOIN users u ON o.customer_id = u.id WHERE o.customer_id = ?";
        return jdbcTemplate.query(sql, orderRowMapper, userId);
    }

    private final RowMapper<Order> orderRowMapper = (rs, rowNum) -> Order.builder()
            .id(rs.getLong("id"))
            .customer(User.builder()
                    .id(rs.getLong("user_id"))
                    .name(rs.getString("name"))
                    .phone(rs.getLong("phone"))
                    .build())
            .dishes(List.of())
            .status(OrderStatus.valueOf(rs.getString("status")))
            .totalPrice(rs.getDouble("total_price"))
            .build();
}
