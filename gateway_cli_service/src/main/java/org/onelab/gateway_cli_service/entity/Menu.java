package org.onelab.gateway_cli_service.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.util.List;

@Document(indexName = "menu",createIndex = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Menu {
    @Id
    private String id;

    @Field(type = FieldType.Text, name = "name", analyzer = "standard")
    private String name;

    @Field(type = FieldType.Nested, name = "dishes")
    private List<Dish> dishes;
}