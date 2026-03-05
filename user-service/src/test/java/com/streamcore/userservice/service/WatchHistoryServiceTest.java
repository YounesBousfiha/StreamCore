package com.streamcore.userservice.service;

import com.streamcore.userservice.client.VideoServiceClient;
import com.streamcore.userservice.client.dto.VideoResponseDto;
import com.streamcore.userservice.dto.request.WatchHistoryRequest;
import com.streamcore.userservice.dto.response.WatchHistoryResponse;
import com.streamcore.userservice.entity.WatchHistory;
import com.streamcore.userservice.exception.UserBadRequestException;
import com.streamcore.userservice.exception.UserResourceNotFoundException;
import com.streamcore.userservice.mapper.WatchHistoryMapper;
import com.streamcore.userservice.repository.UserRepository;
import com.streamcore.userservice.repository.WatchHistoryRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WatchHistoryService")
class WatchHistoryServiceTest {

    @Mock
    private WatchHistoryRepository watchHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VideoServiceClient videoServiceClient;

    @Mock
    private WatchHistoryMapper watchHistoryMapper;

    @InjectMocks
    private WatchHistoryService watchHistoryService;

    private WatchHistory entity;
    private WatchHistoryResponse response;
    private WatchHistoryRequest request;
    private VideoResponseDto videoResponse;

    @BeforeEach
    void setUp() {
        entity = WatchHistory.builder()
                .id(1L)
                .userId(10L)
                .videoId(20L)
                .watchedAt(Instant.parse("2026-03-05T10:00:00Z"))
                .progressTime(1200)
                .completed(true)
                .build();

        response = WatchHistoryResponse.builder()
                .id(1L)
                .userId(10L)
                .videoId(20L)
                .watchedAt(Instant.parse("2026-03-05T10:00:00Z"))
                .progressTime(1200)
                .completed(true)
                .build();

        request = WatchHistoryRequest.builder()
                .videoId(20L)
                .progressTime(1200)
                .completed(true)
                .build();

        videoResponse = new VideoResponseDto();
        videoResponse.setId(20L);
        videoResponse.setTitle("Test Video");
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {

        @Test
        @DisplayName("returns watch history for existing user with limit")
        void returnsWatchHistoryForExistingUser() {
            List<WatchHistory> entities = List.of(entity);
            List<WatchHistoryResponse> responses = List.of(response);

            when(userRepository.existsById(10L)).thenReturn(true);
            when(watchHistoryRepository.findByUserIdOrderByWatchedAtDesc(eq(10L), any(PageRequest.class)))
                    .thenReturn(entities);
            when(watchHistoryMapper.toResponseList(entities)).thenReturn(responses);

            List<WatchHistoryResponse> result = watchHistoryService.findByUserId(10L, 10);

            assertThat(result).isSameAs(responses);
            verify(userRepository).existsById(10L);
            verify(watchHistoryRepository).findByUserIdOrderByWatchedAtDesc(eq(10L), any(PageRequest.class));
            verify(watchHistoryMapper).toResponseList(entities);
        }

        @Test
        @DisplayName("throws UserResourceNotFoundException when user not found")
        void throwsWhenUserNotFound() {
            when(userRepository.existsById(10L)).thenReturn(false);

            assertThatThrownBy(() -> watchHistoryService.findByUserId(10L, 10))
                    .isInstanceOf(UserResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id : '10'");

            verify(userRepository).existsById(10L);
            verify(watchHistoryRepository, never()).findByUserIdOrderByWatchedAtDesc(any(), any());
        }
    }

    @Nested
    @DisplayName("logWatchHistory")
    class LogWatchHistory {

        @Test
        @DisplayName("logs watch history successfully")
        void logsWatchHistorySuccessfully() {
            when(userRepository.existsById(10L)).thenReturn(true);
            when(videoServiceClient.getVideoById(20L)).thenReturn(videoResponse);
            when(watchHistoryRepository.save(any(WatchHistory.class))).thenReturn(entity);
            when(watchHistoryMapper.toResponse(entity)).thenReturn(response);

            WatchHistoryResponse result = watchHistoryService.logWatchHistory(10L, request);

            assertThat(result).isSameAs(response);
            verify(userRepository).existsById(10L);
            verify(videoServiceClient).getVideoById(20L);
            verify(watchHistoryRepository).save(any(WatchHistory.class));
            verify(watchHistoryMapper).toResponse(entity);
        }

        @Test
        @DisplayName("logs watch history with default progressTime when null")
        void logsWithDefaultProgressTime() {
            WatchHistoryRequest requestNoProgress = WatchHistoryRequest.builder()
                    .videoId(20L)
                    .progressTime(null)
                    .completed(false)
                    .build();

            when(userRepository.existsById(10L)).thenReturn(true);
            when(videoServiceClient.getVideoById(20L)).thenReturn(videoResponse);
            when(watchHistoryRepository.save(any(WatchHistory.class))).thenReturn(entity);
            when(watchHistoryMapper.toResponse(entity)).thenReturn(response);

            WatchHistoryResponse result = watchHistoryService.logWatchHistory(10L, requestNoProgress);

            assertThat(result).isSameAs(response);
            verify(watchHistoryRepository).save(any(WatchHistory.class));
        }

        @Test
        @DisplayName("throws UserResourceNotFoundException when user not found")
        void throwsWhenUserNotFound() {
            when(userRepository.existsById(10L)).thenReturn(false);

            assertThatThrownBy(() -> watchHistoryService.logWatchHistory(10L, request))
                    .isInstanceOf(UserResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id : '10'");

            verify(userRepository).existsById(10L);
            verify(videoServiceClient, never()).getVideoById(any());
            verify(watchHistoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws UserBadRequestException when video not found")
        void throwsWhenVideoNotFound() {
            when(userRepository.existsById(10L)).thenReturn(true);
            when(videoServiceClient.getVideoById(20L)).thenThrow(mock(FeignException.NotFound.class));

            assertThatThrownBy(() -> watchHistoryService.logWatchHistory(10L, request))
                    .isInstanceOf(UserBadRequestException.class)
                    .hasMessageContaining("Video with id 20 does not exist");

            verify(userRepository).existsById(10L);
            verify(videoServiceClient).getVideoById(20L);
            verify(watchHistoryRepository, never()).save(any());
        }
    }
}

