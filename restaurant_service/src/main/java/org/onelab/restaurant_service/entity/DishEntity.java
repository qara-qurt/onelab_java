package org.onelab.restaurant_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dishes")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DishEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false, length = 500)
    private String description;
}
