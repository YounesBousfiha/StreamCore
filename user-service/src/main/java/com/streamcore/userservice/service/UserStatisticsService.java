package com.streamcore.userservice.service;

import com.streamcore.userservice.client.VideoServiceClient;
import com.streamcore.userservice.client.dto.VideoResponseDto;
import com.streamcore.userservice.dto.response.UserStatisticsResponse;
import com.streamcore.userservice.entity.WatchHistory;
import com.streamcore.userservice.exception.UserResourceNotFoundException;
import com.streamcore.userservice.repository.UserRepository;
import com.streamcore.userservice.repository.WatchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStatisticsService {

    private final UserRepository userRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final VideoServiceClient videoServiceClient;

    public UserStatisticsResponse getStatistics(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserResourceNotFoundException("User", "id", userId);
        }

        List<WatchHistory> historyList = watchHistoryRepository.findByUserId(userId);

        long totalVideosWatched = historyList.stream()
                .map(WatchHistory::getVideoId)
                .distinct()
                .count();

        long totalWatchTimeSeconds = historyList.stream()
                .mapToLong(wh -> (wh.getProgressTime() != null ? wh.getProgressTime() : 0L))
                .sum();

        List<Long> distinctVideoIds = historyList.stream()
                .map(WatchHistory::getVideoId)
                .distinct()
                .toList();

        String mostWatchedCategory = null;
        if (!distinctVideoIds.isEmpty()) {
            List<VideoResponseDto> videos = videoServiceClient.getVideosByIds(distinctVideoIds);
            mostWatchedCategory = computeMostWatchedCategory(historyList, videos);
        }

        return UserStatisticsResponse.builder()
                .totalVideosWatched(totalVideosWatched)
                .totalWatchTimeSeconds(totalWatchTimeSeconds)
                .mostWatchedCategory(mostWatchedCategory)
                .build();
    }

    /**
     * Aggregates watch count per category (from WatchHistory entries and video-service categories).
     * Most watched category = category with the highest total watch count.
     */
    private String computeMostWatchedCategory(List<WatchHistory> historyList, List<VideoResponseDto> videos) {
        if (videos == null || videos.isEmpty()) {
            return null;
        }

        Map<Long, Long> watchCountByVideoId = historyList.stream()
                .collect(Collectors.groupingBy(WatchHistory::getVideoId, Collectors.counting()));

        Map<Long, String> categoryByVideoId = videos.stream()
                .filter(v -> v.getCategory() != null)
                .collect(Collectors.toMap(VideoResponseDto::getId, VideoResponseDto::getCategory, (a, b) -> a));

        Map<String, Long> totalWatchCountByCategory = watchCountByVideoId.entrySet().stream()
                .filter(e -> categoryByVideoId.containsKey(e.getKey()))
                .collect(Collectors.groupingBy(
                        e -> categoryByVideoId.get(e.getKey()),
                        Collectors.summingLong(Map.Entry::getValue)
                ));

        return totalWatchCountByCategory.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
