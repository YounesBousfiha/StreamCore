package com.streamcore.userservice.mapper;

import com.streamcore.userservice.dto.response.WatchHistoryResponse;
import com.streamcore.userservice.entity.WatchHistory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WatchHistoryMapper {

    public WatchHistoryResponse toResponse(WatchHistory entity) {
        if (entity == null) return null;
        return WatchHistoryResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .videoId(entity.getVideoId())
                .watchedAt(entity.getWatchedAt())
                .progressTime(entity.getProgressTime())
                .completed(entity.getCompleted())
                .build();
    }

    public List<WatchHistoryResponse> toResponseList(List<WatchHistory> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
