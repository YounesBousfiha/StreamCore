package com.streamcore.userservice.mapper;

import com.streamcore.userservice.dto.response.WatchHistoryResponse;
import com.streamcore.userservice.entity.WatchHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WatchHistoryMapper")
class WatchHistoryMapperTest {

    private WatchHistoryMapper watchHistoryMapper;
    private WatchHistory entity;

    @BeforeEach
    void setUp() {
        watchHistoryMapper = new WatchHistoryMapper();

        entity = WatchHistory.builder()
                .id(1L)
                .userId(10L)
                .videoId(20L)
                .watchedAt(Instant.parse("2026-03-05T10:00:00Z"))
                .progressTime(1200)
                .completed(true)
                .build();
    }

    @Test
    @DisplayName("toResponse maps entity to response")
    void toResponse_mapsEntityToResponse() {
        WatchHistoryResponse result = watchHistoryMapper.toResponse(entity);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(10L);
        assertThat(result.getVideoId()).isEqualTo(20L);
        assertThat(result.getWatchedAt()).isEqualTo(Instant.parse("2026-03-05T10:00:00Z"));
        assertThat(result.getProgressTime()).isEqualTo(1200);
        assertThat(result.getCompleted()).isTrue();
    }

    @Test
    @DisplayName("toResponse returns null when entity is null")
    void toResponse_returnsNullWhenEntityIsNull() {
        WatchHistoryResponse result = watchHistoryMapper.toResponse(null);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toResponseList maps list of entities to responses")
    void toResponseList_mapsListOfEntities() {
        WatchHistory entity2 = WatchHistory.builder()
                .id(2L)
                .userId(10L)
                .videoId(21L)
                .watchedAt(Instant.parse("2026-03-05T11:00:00Z"))
                .progressTime(600)
                .completed(false)
                .build();

        List<WatchHistory> entities = List.of(entity, entity2);
        List<WatchHistoryResponse> result = watchHistoryMapper.toResponseList(entities);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getCompleted()).isTrue();
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getCompleted()).isFalse();
    }

    @Test
    @DisplayName("toResponseList returns empty list when input is null")
    void toResponseList_returnsEmptyListWhenNull() {
        List<WatchHistoryResponse> result = watchHistoryMapper.toResponseList(null);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("toResponseList returns empty list when input is empty")
    void toResponseList_returnsEmptyListWhenEmpty() {
        List<WatchHistoryResponse> result = watchHistoryMapper.toResponseList(List.of());

        assertThat(result).isEmpty();
    }
}

