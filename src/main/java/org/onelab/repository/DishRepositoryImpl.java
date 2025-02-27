package org.onelab.repository;

import org.onelab.entity.Dish;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DishRepositoryImpl implements DishRepository {
    private final JdbcTemplate jdbcTemplate;

    public DishRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Dish dish) {
        String sql = "INSERT INTO dishes (name, price) VALUES (?, ?)";
        jdbcTemplate.update(sql, dish.getName(), dish.getPrice());
    }


    @Override
    public List<Dish> findAll() {
        String sql = "SELECT * FROM dishes";
        return jdbcTemplate.query(sql, dishRowMapper);
    }

    private final RowMapper<Dish> dishRowMapper = (rs, rowNum) -> Dish.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .price(rs.getDouble("price"))
            .build();
}
