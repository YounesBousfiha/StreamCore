package com.streamcore.userservice.mapper;

import com.streamcore.userservice.dto.request.UserRequest;
import com.streamcore.userservice.dto.response.UserResponse;
import com.streamcore.userservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserMapper")
class UserMapperTest {

    private UserMapper userMapper;
    private User entity;
    private UserRequest request;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();

        entity = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        request = UserRequest.builder()
                .username("newuser")
                .email("new@example.com")
                .password("newpass123")
                .build();
    }

    @Test
    @DisplayName("toEntity maps request to entity without id")
    void toEntity_mapsRequestToEntity() {
        User result = userMapper.toEntity(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getPassword()).isEqualTo("newpass123");
    }

    @Test
    @DisplayName("toEntity returns null when request is null")
    void toEntity_returnsNullWhenRequestIsNull() {
        User result = userMapper.toEntity(null);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("updateEntity updates entity fields from request")
    void updateEntity_updatesEntityFields() {
        userMapper.updateEntity(entity, request);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getUsername()).isEqualTo("newuser");
        assertThat(entity.getEmail()).isEqualTo("new@example.com");
        assertThat(entity.getPassword()).isEqualTo("newpass123");
    }

    @Test
    @DisplayName("updateEntity does nothing when entity is null")
    void updateEntity_doesNothingWhenEntityIsNull() {
        userMapper.updateEntity(null, request);
        // No exception should be thrown
    }

    @Test
    @DisplayName("updateEntity does nothing when request is null")
    void updateEntity_doesNothingWhenRequestIsNull() {
        String originalUsername = entity.getUsername();
        userMapper.updateEntity(entity, null);

        assertThat(entity.getUsername()).isEqualTo(originalUsername);
    }

    @Test
    @DisplayName("toResponse maps entity to response without password")
    void toResponse_mapsEntityToResponse() {
        UserResponse result = userMapper.toResponse(entity);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("toResponse returns null when entity is null")
    void toResponse_returnsNullWhenEntityIsNull() {
        UserResponse result = userMapper.toResponse(null);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toResponseList maps list of entities to responses")
    void toResponseList_mapsListOfEntities() {
        User entity2 = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .password("pass2")
                .build();

        List<User> entities = List.of(entity, entity2);
        List<UserResponse> result = userMapper.toResponseList(entities);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getUsername()).isEqualTo("user2");
    }

    @Test
    @DisplayName("toResponseList returns empty list when input is null")
    void toResponseList_returnsEmptyListWhenNull() {
        List<UserResponse> result = userMapper.toResponseList(null);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("toResponseList returns empty list when input is empty")
    void toResponseList_returnsEmptyListWhenEmpty() {
        List<UserResponse> result = userMapper.toResponseList(List.of());

        assertThat(result).isEmpty();
    }
}

