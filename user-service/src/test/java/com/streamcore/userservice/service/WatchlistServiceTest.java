package com.streamcore.userservice.service;

import com.streamcore.userservice.client.VideoServiceClient;
import com.streamcore.userservice.client.dto.VideoResponseDto;
import com.streamcore.userservice.dto.request.WatchlistRequest;
import com.streamcore.userservice.dto.response.WatchlistResponse;
import com.streamcore.userservice.entity.Watchlist;
import com.streamcore.userservice.exception.UserBadRequestException;
import com.streamcore.userservice.exception.UserResourceNotFoundException;
import com.streamcore.userservice.mapper.WatchlistMapper;
import com.streamcore.userservice.repository.UserRepository;
import com.streamcore.userservice.repository.WatchlistRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WatchlistService")
class WatchlistServiceTest {

    @Mock
    private WatchlistRepository watchlistRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VideoServiceClient videoServiceClient;

    @Mock
    private WatchlistMapper watchlistMapper;

    @InjectMocks
    private WatchlistService watchlistService;

    private Watchlist entity;
    private WatchlistResponse response;
    private WatchlistRequest request;
    private VideoResponseDto videoResponse;

    @BeforeEach
    void setUp() {
        entity = Watchlist.builder()
                .id(1L)
                .userId(10L)
                .videoId(20L)
                .addedAt(Instant.parse("2026-03-05T10:00:00Z"))
                .build();

        response = WatchlistResponse.builder()
                .id(1L)
                .userId(10L)
                .videoId(20L)
                .addedAt(Instant.parse("2026-03-05T10:00:00Z"))
                .build();

        request = WatchlistRequest.builder()
                .videoId(20L)
                .build();

        videoResponse = new VideoResponseDto();
        videoResponse.setId(20L);
        videoResponse.setTitle("Test Video");
    }

    @Nested
    @DisplayName("findByUserId")
    class FindByUserId {

        @Test
        @DisplayName("returns watchlist for existing user")
        void returnsWatchlistForExistingUser() {
            List<Watchlist> entities = List.of(entity);
            List<WatchlistResponse> responses = List.of(response);

            when(userRepository.existsById(10L)).thenReturn(true);
            when(watchlistRepository.findByUserIdOrderByAddedAtDesc(10L)).thenReturn(entities);
            when(watchlistMapper.toResponseList(entities)).thenReturn(responses);

            List<WatchlistResponse> result = watchlistService.findByUserId(10L);

            assertThat(result).isSameAs(responses);
            verify(userRepository).existsById(10L);
            verify(watchlistRepository).findByUserIdOrderByAddedAtDesc(10L);
            verify(watchlistMapper).toResponseList(entities);
        }

        @Test
        @DisplayName("throws UserResourceNotFoundException when user not found")
        void throwsWhenUserNotFound() {
            when(userRepository.existsById(10L)).thenReturn(false);

            assertThatThrownBy(() -> watchlistService.findByUserId(10L))
                    .isInstanceOf(UserResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id : '10'");

            verify(userRepository).existsById(10L);
            verify(watchlistRepository, never()).findByUserIdOrderByAddedAtDesc(any());
        }
    }

    @Nested
    @DisplayName("addToWatchlist")
    class AddToWatchlist {

        @Test
        @DisplayName("adds video to watchlist successfully")
        void addsVideoSuccessfully() {
            when(userRepository.existsById(10L)).thenReturn(true);
            when(videoServiceClient.getVideoById(20L)).thenReturn(videoResponse);
            when(watchlistRepository.existsByUserIdAndVideoId(10L, 20L)).thenReturn(false);
            when(watchlistRepository.save(any(Watchlist.class))).thenReturn(entity);
            when(watchlistMapper.toResponse(entity)).thenReturn(response);

            WatchlistResponse result = watchlistService.addToWatchlist(10L, request);

            assertThat(result).isSameAs(response);
            verify(userRepository).existsById(10L);
            verify(videoServiceClient).getVideoById(20L);
            verify(watchlistRepository).existsByUserIdAndVideoId(10L, 20L);
            verify(watchlistRepository).save(any(Watchlist.class));
            verify(watchlistMapper).toResponse(entity);
        }

        @Test
        @DisplayName("throws UserResourceNotFoundException when user not found")
        void throwsWhenUserNotFound() {
            when(userRepository.existsById(10L)).thenReturn(false);

            assertThatThrownBy(() -> watchlistService.addToWatchlist(10L, request))
                    .isInstanceOf(UserResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id : '10'");

            verify(userRepository).existsById(10L);
            verify(videoServiceClient, never()).getVideoById(any());
            verify(watchlistRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws UserBadRequestException when video not found")
        void throwsWhenVideoNotFound() {
            when(userRepository.existsById(10L)).thenReturn(true);
            when(videoServiceClient.getVideoById(20L)).thenThrow(mock(FeignException.NotFound.class));

            assertThatThrownBy(() -> watchlistService.addToWatchlist(10L, request))
                    .isInstanceOf(UserBadRequestException.class)
                    .hasMessageContaining("Video with id 20 does not exist");

            verify(userRepository).existsById(10L);
            verify(videoServiceClient).getVideoById(20L);
            verify(watchlistRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws UserBadRequestException when video already in watchlist")
        void throwsWhenVideoAlreadyInWatchlist() {
            when(userRepository.existsById(10L)).thenReturn(true);
            when(videoServiceClient.getVideoById(20L)).thenReturn(videoResponse);
            when(watchlistRepository.existsByUserIdAndVideoId(10L, 20L)).thenReturn(true);

            assertThatThrownBy(() -> watchlistService.addToWatchlist(10L, request))
                    .isInstanceOf(UserBadRequestException.class)
                    .hasMessageContaining("Video already in watchlist");

            verify(userRepository).existsById(10L);
            verify(videoServiceClient).getVideoById(20L);
            verify(watchlistRepository).existsByUserIdAndVideoId(10L, 20L);
            verify(watchlistRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("removeFromWatchlist")
    class RemoveFromWatchlist {

        @Test
        @DisplayName("removes video from watchlist successfully")
        void removesVideoSuccessfully() {
            when(userRepository.existsById(10L)).thenReturn(true);
            when(watchlistRepository.findByUserIdAndVideoId(10L, 20L)).thenReturn(Optional.of(entity));

            watchlistService.removeFromWatchlist(10L, 20L);

            verify(userRepository).existsById(10L);
            verify(watchlistRepository).findByUserIdAndVideoId(10L, 20L);
            verify(watchlistRepository).delete(entity);
        }

        @Test
        @DisplayName("throws UserResourceNotFoundException when user not found")
        void throwsWhenUserNotFound() {
            when(userRepository.existsById(10L)).thenReturn(false);

            assertThatThrownBy(() -> watchlistService.removeFromWatchlist(10L, 20L))
                    .isInstanceOf(UserResourceNotFoundException.class)
                    .hasMessageContaining("User not found with id : '10'");

            verify(userRepository).existsById(10L);
            verify(watchlistRepository, never()).findByUserIdAndVideoId(any(), any());
            verify(watchlistRepository, never()).delete(any());
        }

        @Test
        @DisplayName("throws UserResourceNotFoundException when watchlist entry not found")
        void throwsWhenWatchlistEntryNotFound() {
            when(userRepository.existsById(10L)).thenReturn(true);
            when(watchlistRepository.findByUserIdAndVideoId(10L, 20L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> watchlistService.removeFromWatchlist(10L, 20L))
                    .isInstanceOf(UserResourceNotFoundException.class)
                    .hasMessageContaining("Watchlist entry not found with userId/videoId : '10/20'");

            verify(userRepository).existsById(10L);
            verify(watchlistRepository).findByUserIdAndVideoId(10L, 20L);
            verify(watchlistRepository, never()).delete(any());
        }
    }
}

