package com.streamcore.userservice.service;

import com.streamcore.userservice.dto.request.UserRequest;
import com.streamcore.userservice.dto.response.UserResponse;
import com.streamcore.userservice.entity.User;
import com.streamcore.userservice.exception.UserBadRequestException;
import com.streamcore.userservice.exception.UserResourceNotFoundException;
import com.streamcore.userservice.mapper.UserMapper;
import com.streamcore.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User entity;
    private UserResponse response;
    private UserRequest request;

    @BeforeEach
    void setUp() {
        entity = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        response = UserResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        request = UserRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("returns list from mapper when repository returns entities")
        void returnsMappedList() {
            List<User> entities = List.of(entity);
            List<UserResponse> responses = List.of(response);

            when(userRepository.findAll()).thenReturn(entities);
            when(userMapper.toResponseList(entities)).thenReturn(responses);

            List<UserResponse> result = userService.findAll();

            assertThat(result).isSameAs(responses);
            verify(userRepository).findAll();
            verify(userMapper).toResponseList(entities);
        }

        @Test
        @DisplayName("returns empty list when repository returns empty")
        void returnsEmptyWhenNoData() {
            when(userRepository.findAll()).thenReturn(List.of());
            when(userMapper.toResponseList(List.of())).thenReturn(List.of());

            List<UserResponse> result = userService.findAll();

            assertThat(result).isEmpty();
            verify(userMapper).toResponseList(List.of());
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("returns mapped response when user exists")
        void returnsResponseWhenFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(userMapper.toResponse(entity)).thenReturn(response);

            UserResponse result = userService.findById(1L);

            assertThat(result).isSameAs(response);
            verify(userRepository).findById(1L);
            verify(userMapper).toResponse(entity);
        }

        @Test
        @DisplayName("throws UserResourceNotFoundException when user not found")
        void throwsWhenNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.findById(1L))
                    .isInstanceOf(UserResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id : '1'");

            verify(userRepository).findById(1L);
            verify(userMapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("creates user successfully when username and email are unique")
        void createsUserSuccessfully() {
            when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(userMapper.toEntity(request)).thenReturn(entity);
            when(userRepository.save(entity)).thenReturn(entity);
            when(userMapper.toResponse(entity)).thenReturn(response);

            UserResponse result = userService.create(request);

            assertThat(result).isSameAs(response);
            verify(userRepository).existsByUsername(request.getUsername());
            verify(userRepository).existsByEmail(request.getEmail());
            verify(userMapper).toEntity(request);
            verify(userRepository).save(entity);
            verify(userMapper).toResponse(entity);
        }

        @Test
        @DisplayName("throws UserBadRequestException when username already exists")
        void throwsWhenUsernameExists() {
            when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

            assertThatThrownBy(() -> userService.create(request))
                    .isInstanceOf(UserBadRequestException.class)
                    .hasMessageContaining("Username already exists");

            verify(userRepository).existsByUsername(request.getUsername());
            verify(userRepository, never()).existsByEmail(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws UserBadRequestException when email already exists")
        void throwsWhenEmailExists() {
            when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

            assertThatThrownBy(() -> userService.create(request))
                    .isInstanceOf(UserBadRequestException.class)
                    .hasMessageContaining("Email already exists");

            verify(userRepository).existsByUsername(request.getUsername());
            verify(userRepository).existsByEmail(request.getEmail());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("updates user successfully when user exists")
        void updatesUserSuccessfully() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(userRepository.save(entity)).thenReturn(entity);
            when(userMapper.toResponse(entity)).thenReturn(response);

            UserResponse result = userService.update(1L, request);

            assertThat(result).isSameAs(response);
            verify(userRepository).findById(1L);
            verify(userMapper).updateEntity(entity, request);
            verify(userRepository).save(entity);
            verify(userMapper).toResponse(entity);
        }

        @Test
        @DisplayName("throws UserResourceNotFoundException when user not found")
        void throwsWhenUserNotFound() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.update(1L, request))
                    .isInstanceOf(UserResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id : '1'");

            verify(userRepository).findById(1L);
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws UserBadRequestException when new username already exists")
        void throwsWhenNewUsernameExists() {
            UserRequest newRequest = UserRequest.builder()
                    .username("newusername")
                    .email("test@example.com")
                    .password("password123")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(userRepository.existsByUsername(newRequest.getUsername())).thenReturn(true);

            assertThatThrownBy(() -> userService.update(1L, newRequest))
                    .isInstanceOf(UserBadRequestException.class)
                    .hasMessageContaining("Username already exists");

            verify(userRepository).findById(1L);
            verify(userRepository).existsByUsername(newRequest.getUsername());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws UserBadRequestException when new email already exists")
        void throwsWhenNewEmailExists() {
            UserRequest newRequest = UserRequest.builder()
                    .username("testuser")
                    .email("newemail@example.com")
                    .password("password123")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(userRepository.existsByEmail(newRequest.getEmail())).thenReturn(true);

            assertThatThrownBy(() -> userService.update(1L, newRequest))
                    .isInstanceOf(UserBadRequestException.class)
                    .hasMessageContaining("Email already exists");

            verify(userRepository).findById(1L);
            verify(userRepository).existsByEmail(newRequest.getEmail());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("allows same username and email when updating same user")
        void allowsSameCredentialsForSameUser() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(userRepository.save(entity)).thenReturn(entity);
            when(userMapper.toResponse(entity)).thenReturn(response);

            UserResponse result = userService.update(1L, request);

            assertThat(result).isSameAs(response);
            verify(userRepository).findById(1L);
            verify(userRepository, never()).existsByUsername(any());
            verify(userRepository, never()).existsByEmail(any());
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteById {

        @Test
        @DisplayName("deletes user when user exists")
        void deletesUserSuccessfully() {
            when(userRepository.existsById(1L)).thenReturn(true);

            userService.deleteById(1L);

            verify(userRepository).existsById(1L);
            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("throws UserResourceNotFoundException when user not found")
        void throwsWhenUserNotFound() {
            when(userRepository.existsById(1L)).thenReturn(false);

            assertThatThrownBy(() -> userService.deleteById(1L))
                    .isInstanceOf(UserResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id : '1'");

            verify(userRepository).existsById(1L);
            verify(userRepository, never()).deleteById(any());
        }
    }
}

