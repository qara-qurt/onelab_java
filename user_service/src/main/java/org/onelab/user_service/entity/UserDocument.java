package org.onelab.user_service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.List;

@Document(indexName = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String surname;

    @Field(type = FieldType.Keyword)
    private String username;

    @Field(type = FieldType.Keyword, name = "phone")
    private String phone;

    @Field(type = FieldType.Keyword, name = "password")
    private String password;

    @Field(type = FieldType.Double, name = "balance")
    private Double balance;

    @Field(type = FieldType.Keyword, name = "roles")
    private List<String> roles;

    @Field(type = FieldType.Date, name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant createdAt;

    @Field(type = FieldType.Date, name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Instant updatedAt;

    @Field(type = FieldType.Boolean, name = "is_active")
    private boolean isActive;
}
