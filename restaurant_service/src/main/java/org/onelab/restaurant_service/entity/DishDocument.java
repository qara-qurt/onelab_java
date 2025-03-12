package org.onelab.restaurant_service.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "dishes")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DishDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text, name = "name", analyzer = "standard")
    private String name;

    @Field(type = FieldType.Double, name = "price")
    private Double price;

    @Field(type = FieldType.Text, name = "description", analyzer = "standard")
    private String description;
}