package org.onelab.restaurant_service.entity;

import lombok.*;
import org.onelab.common_lib.enums.OrderStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.util.List;

@Document(indexName = "orders")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDocument {
    @Id
    private String id;

    @Field(type = FieldType.Keyword, name = "customer_id")
    private String customerId;

    @Field(type = FieldType.Nested, name = "dishes")
    private List<DishDocument> dishes;

    @Field(type = FieldType.Keyword, name = "status")
    private OrderStatus status;

    @Field(type = FieldType.Double, name = "total_price")
    private Double totalPrice;
}