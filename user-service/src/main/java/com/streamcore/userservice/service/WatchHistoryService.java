package com.streamcore.userservice.service;

import com.streamcore.userservice.client.VideoServiceClient;
import com.streamcore.userservice.dto.request.WatchHistoryRequest;
import com.streamcore.userservice.dto.response.WatchHistoryResponse;
import com.streamcore.userservice.entity.WatchHistory;
import com.streamcore.userservice.exception.UserBadRequestException;
import com.streamcore.userservice.exception.UserResourceNotFoundException;
import com.streamcore.userservice.mapper.WatchHistoryMapper;
import com.streamcore.userservice.repository.UserRepository;
import com.streamcore.userservice.repository.WatchHistoryRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchHistoryService {

    private final WatchHistoryRepository watchHistoryRepository;
    private final UserRepository userRepository;
    private final VideoServiceClient videoServiceClient;
    private final WatchHistoryMapper watchHistoryMapper;

    public List<WatchHistoryResponse> findByUserId(Long userId, int limit) {
        ensureUserExists(userId);
        return watchHistoryMapper.toResponseList(
                watchHistoryRepository.findByUserIdOrderByWatchedAtDesc(userId, PageRequest.of(0, limit)));
    }

    @Transactional
    public WatchHistoryResponse logWatchHistory(Long userId, WatchHistoryRequest request) {
        ensureUserExists(userId);
        Long videoId = request.getVideoId();
        verifyVideoExists(videoId);
        WatchHistory history = WatchHistory.builder()
                .userId(userId)
                .videoId(videoId)
                .watchedAt(Instant.now())
                .progressTime(request.getProgressTime() != null ? request.getProgressTime() : 0)
                .completed(Boolean.TRUE.equals(request.getCompleted()))
                .build();
        history = watchHistoryRepository.save(history);
        return watchHistoryMapper.toResponse(history);
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
