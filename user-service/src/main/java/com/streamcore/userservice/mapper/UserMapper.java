package com.streamcore.userservice.mapper;

import com.streamcore.userservice.dto.request.UserRequest;
import com.streamcore.userservice.dto.response.UserResponse;
import com.streamcore.userservice.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        if (request == null) return null;
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public void updateEntity(User entity, UserRequest request) {
        if (entity == null || request == null) return;
        entity.setUsername(request.getUsername());
        entity.setEmail(request.getEmail());
        entity.setPassword(request.getPassword());
    }

    public UserResponse toResponse(User entity) {
        if (entity == null) return null;
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .build();
    }

    public List<UserResponse> toResponseList(List<User> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
