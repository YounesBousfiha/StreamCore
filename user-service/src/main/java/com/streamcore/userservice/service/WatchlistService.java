package com.streamcore.userservice.service;

import com.streamcore.userservice.client.VideoServiceClient;
import com.streamcore.userservice.dto.request.WatchlistRequest;
import com.streamcore.userservice.dto.response.WatchlistResponse;
import com.streamcore.userservice.entity.Watchlist;
import com.streamcore.userservice.exception.UserBadRequestException;
import com.streamcore.userservice.exception.UserResourceNotFoundException;
import com.streamcore.userservice.mapper.WatchlistMapper;
import com.streamcore.userservice.repository.UserRepository;
import com.streamcore.userservice.repository.WatchlistRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;
    private final VideoServiceClient videoServiceClient;
    private final WatchlistMapper watchlistMapper;

    public List<WatchlistResponse> findByUserId(Long userId) {
        ensureUserExists(userId);
        return watchlistMapper.toResponseList(watchlistRepository.findByUserIdOrderByAddedAtDesc(userId));
    }

    @Transactional
    public WatchlistResponse addToWatchlist(Long userId, WatchlistRequest request) {
        ensureUserExists(userId);
        Long videoId = request.getVideoId();
        verifyVideoExists(videoId);
        if (watchlistRepository.existsByUserIdAndVideoId(userId, videoId)) {
            throw new UserBadRequestException("Video already in watchlist");
        }
        Watchlist watchlist = Watchlist.builder()
                .userId(userId)
                .videoId(videoId)
                .addedAt(Instant.now())
                .build();
        watchlist = watchlistRepository.save(watchlist);
        return watchlistMapper.toResponse(watchlist);
    }

    @Transactional
    public void removeFromWatchlist(Long userId, Long videoId) {
        ensureUserExists(userId);
        Watchlist watchlist = watchlistRepository.findByUserIdAndVideoId(userId, videoId)
                .orElseThrow(() -> new UserResourceNotFoundException("Watchlist entry", "userId/videoId", userId + "/" + videoId));
        watchlistRepository.delete(watchlist);
    }

    private void ensureUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserResourceNotFoundException("User", "id", userId);
        }
    }

    private void verifyVideoExists(Long videoId) {
        try {
            videoServiceClient.getVideoById(videoId);
        } catch (FeignException.NotFound e) {
            throw new UserBadRequestException("Video with id " + videoId + " does not exist");
        }
    }
}
