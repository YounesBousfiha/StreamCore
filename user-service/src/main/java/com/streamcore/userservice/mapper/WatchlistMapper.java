package com.streamcore.userservice.mapper;

import com.streamcore.userservice.dto.response.WatchlistResponse;
import com.streamcore.userservice.entity.Watchlist;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class WatchlistMapper {

    public WatchlistResponse toResponse(Watchlist entity) {
        if (entity == null) return null;
        return WatchlistResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .videoId(entity.getVideoId())
                .addedAt(entity.getAddedAt())
                .build();
    }

    public List<WatchlistResponse> toResponseList(List<Watchlist> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
