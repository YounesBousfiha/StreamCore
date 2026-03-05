package com.streamcore.userservice.mapper;

import com.streamcore.userservice.dto.response.WatchlistResponse;
import com.streamcore.userservice.entity.Watchlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WatchlistMapper")
class WatchlistMapperTest {

    private WatchlistMapper watchlistMapper;
    private Watchlist entity;

    @BeforeEach
    void setUp() {
        watchlistMapper = new WatchlistMapper();

        entity = Watchlist.builder()
                .id(1L)
                .userId(10L)
                .videoId(20L)
                .addedAt(Instant.parse("2026-03-05T10:00:00Z"))
                .build();
    }

    @Test
    @DisplayName("toResponse maps entity to response")
    void toResponse_mapsEntityToResponse() {
        WatchlistResponse result = watchlistMapper.toResponse(entity);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(10L);
        assertThat(result.getVideoId()).isEqualTo(20L);
        assertThat(result.getAddedAt()).isEqualTo(Instant.parse("2026-03-05T10:00:00Z"));
    }

    @Test
    @DisplayName("toResponse returns null when entity is null")
    void toResponse_returnsNullWhenEntityIsNull() {
        WatchlistResponse result = watchlistMapper.toResponse(null);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("toResponseList maps list of entities to responses")
    void toResponseList_mapsListOfEntities() {
        Watchlist entity2 = Watchlist.builder()
                .id(2L)
                .userId(10L)
                .videoId(21L)
                .addedAt(Instant.parse("2026-03-05T11:00:00Z"))
                .build();

        List<Watchlist> entities = List.of(entity, entity2);
        List<WatchlistResponse> result = watchlistMapper.toResponseList(entities);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getVideoId()).isEqualTo(20L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getVideoId()).isEqualTo(21L);
    }

    @Test
    @DisplayName("toResponseList returns empty list when input is null")
    void toResponseList_returnsEmptyListWhenNull() {
        List<WatchlistResponse> result = watchlistMapper.toResponseList(null);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("toResponseList returns empty list when input is empty")
    void toResponseList_returnsEmptyListWhenEmpty() {
        List<WatchlistResponse> result = watchlistMapper.toResponseList(List.of());

        assertThat(result).isEmpty();
    }
}

