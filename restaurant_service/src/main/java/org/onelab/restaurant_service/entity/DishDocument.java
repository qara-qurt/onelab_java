package org.onelab.restaurant_service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

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

    @Field(type = FieldType.Date, name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;

    @Field(type = FieldType.Date, name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant updatedAt;
}