package org.onelab.user_service.mapper;

import org.onelab.user_service.dto.UserDto;
import org.onelab.user_service.entity.Role;
import org.onelab.user_service.entity.UserDocument;
import org.onelab.user_service.entity.UserEntity;

import java.util.Collections;

public class UserMapper {
    public static UserDto toDto(UserEntity entity) {
        return UserDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .surname(entity.getSurname())
                .username(entity.getUsername())
                .phone(entity.getPhone())
                .balance(entity.getBalance())
                .roles(entity.getRoles())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isActive(entity.isActive())
                .build();
    }

    public static UserDto toDto(UserDocument document) {
        return UserDto.builder()
                .id(Long.valueOf(document.getId()))
                .name(document.getName())
                .surname(document.getSurname())
                .username(document.getUsername())
                .phone(document.getPhone())
                .balance(document.getBalance())
                .roles(document.getRoles() != null
                        ? document.getRoles().stream().map(Role::valueOf).toList()
                        : Collections.emptyList()
                )
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .isActive(document.isActive())
                .build();
    }


    public static UserEntity toEntity(UserDto dto) {
        return UserEntity.builder()
                .name(dto.getName())
                .surname(dto.getSurname())
                .username(dto.getUsername())
                .phone(dto.getPhone())
                .password(dto.getPassword())
                .balance(dto.getBalance())
                .roles(dto.getRoles())
                .isActive(dto.isActive())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }


    public static UserDocument toDocument(UserEntity entity) {
        return new UserDocument(
                entity.getId().toString(),
                entity.getName(),
                entity.getSurname(),
                entity.getUsername(),
                entity.getPhone(),
                entity.getPassword(),
                entity.getBalance(),
                entity.getRoles().stream().map(Enum::name).toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isActive()
        );
    }
}
