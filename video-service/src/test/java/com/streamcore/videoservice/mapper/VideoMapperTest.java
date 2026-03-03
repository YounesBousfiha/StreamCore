package com.streamcore.videoservice.mapper;

import com.streamcore.videoservice.dto.request.VideoRequest;
import com.streamcore.videoservice.dto.response.VideoResponse;
import com.streamcore.videoservice.entity.Video;
import com.streamcore.videoservice.enums.Category;
import com.streamcore.videoservice.enums.VideoType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VideoMapper")
class VideoMapperTest {

    private VideoMapper videoMapper;

    private Video entity;
    private VideoRequest request;

    @BeforeEach
    void setUp() {
        videoMapper = new VideoMapper();

        entity = Video.builder()
                .id(1L)
                .title("Test Film")
                .description("Description")
                .thumbnailUrl("http://thumb")
                .trailerUrl("http://trailer")
                .duration(120)
                .releaseYear(2024)
                .type(VideoType.FILM)
                .category(Category.ACTION)
                .rating(8.5)
                .director("Director")
                .cast(List.of("Actor1", "Actor2"))
                .build();

        request = VideoRequest.builder()
                .title("Request Title")
                .description("Request Description")
                .thumbnailUrl("http://req-thumb")
                .trailerUrl("http://req-trailer")
                .duration(90)
                .releaseYear(2023)
                .type(VideoType.SERIE)
                .category(Category.COMEDIE)
                .rating(7.0)
                .director("Req Director")
                .cast(List.of("Cast1"))
                .build();
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("returns null when request is null")
        void returnsNullWhenRequestNull() {
            assertThat(videoMapper.toEntity(null)).isNull();
        }

        @Test
        @DisplayName("maps all request fields to entity")
        void mapsAllFields() {
            Video result = videoMapper.toEntity(request);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isNull();
            assertThat(result.getTitle()).isEqualTo("Request Title");
            assertThat(result.getDescription()).isEqualTo("Request Description");
            assertThat(result.getThumbnailUrl()).isEqualTo("http://req-thumb");
            assertThat(result.getTrailerUrl()).isEqualTo("http://req-trailer");
            assertThat(result.getDuration()).isEqualTo(90);
            assertThat(result.getReleaseYear()).isEqualTo(2023);
            assertThat(result.getType()).isEqualTo(VideoType.SERIE);
            assertThat(result.getCategory()).isEqualTo(Category.COMEDIE);
            assertThat(result.getRating()).isEqualTo(7.0);
            assertThat(result.getDirector()).isEqualTo("Req Director");
            assertThat(result.getCast()).containsExactly("Cast1");
        }

        @Test
        @DisplayName("handles request with null optional fields")
        void handlesNullOptionals() {
            VideoRequest minimal = VideoRequest.builder()
                    .title("Title")
                    .type(VideoType.FILM)
                    .category(Category.DRAME)
                    .build();

            Video result = videoMapper.toEntity(minimal);

            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Title");
            assertThat(result.getType()).isEqualTo(VideoType.FILM);
            assertThat(result.getCategory()).isEqualTo(Category.DRAME);
            assertThat(result.getDescription()).isNull();
            assertThat(result.getCast()).isNull();
        }
    }

    @Nested
    @DisplayName("updateEntity")
    class UpdateEntity {

        @Test
        @DisplayName("does nothing when entity is null")
        void noOpWhenEntityNull() {
            videoMapper.updateEntity(null, request);
            // no exception
        }

        @Test
        @DisplayName("does nothing when request is null")
        void noOpWhenRequestNull() {
            String originalTitle = entity.getTitle();
            videoMapper.updateEntity(entity, null);
            assertThat(entity.getTitle()).isEqualTo(originalTitle);
        }

        @Test
        @DisplayName("does nothing when both are null")
        void noOpWhenBothNull() {
            videoMapper.updateEntity(null, null);
        }

        @Test
        @DisplayName("updates all entity fields from request")
        void updatesAllFields() {
            videoMapper.updateEntity(entity, request);

            assertThat(entity.getTitle()).isEqualTo("Request Title");
            assertThat(entity.getDescription()).isEqualTo("Request Description");
            assertThat(entity.getThumbnailUrl()).isEqualTo("http://req-thumb");
            assertThat(entity.getTrailerUrl()).isEqualTo("http://req-trailer");
            assertThat(entity.getDuration()).isEqualTo(90);
            assertThat(entity.getReleaseYear()).isEqualTo(2023);
            assertThat(entity.getType()).isEqualTo(VideoType.SERIE);
            assertThat(entity.getCategory()).isEqualTo(Category.COMEDIE);
            assertThat(entity.getRating()).isEqualTo(7.0);
            assertThat(entity.getDirector()).isEqualTo("Req Director");
            assertThat(entity.getCast()).containsExactly("Cast1");
            assertThat(entity.getId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("toResponse")
    class ToResponse {

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityNull() {
            assertThat(videoMapper.toResponse(null)).isNull();
        }

        @Test
        @DisplayName("maps all entity fields to response")
        void mapsAllFields() {
            VideoResponse result = videoMapper.toResponse(entity);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("Test Film");
            assertThat(result.getDescription()).isEqualTo("Description");
            assertThat(result.getThumbnailUrl()).isEqualTo("http://thumb");
            assertThat(result.getTrailerUrl()).isEqualTo("http://trailer");
            assertThat(result.getDuration()).isEqualTo(120);
            assertThat(result.getReleaseYear()).isEqualTo(2024);
            assertThat(result.getType()).isEqualTo(VideoType.FILM);
            assertThat(result.getCategory()).isEqualTo(Category.ACTION);
            assertThat(result.getRating()).isEqualTo(8.5);
            assertThat(result.getDirector()).isEqualTo("Director");
            assertThat(result.getCast()).containsExactly("Actor1", "Actor2");
        }

        @Test
        @DisplayName("handles entity with null optional fields")
        void handlesNullOptionals() {
            Video minimal = Video.builder()
                    .id(2L)
                    .title("Minimal")
                    .type(VideoType.SERIE)
                    .category(Category.THRILLER)
                    .build();

            VideoResponse result = videoMapper.toResponse(minimal);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getTitle()).isEqualTo("Minimal");
            assertThat(result.getType()).isEqualTo(VideoType.SERIE);
            assertThat(result.getCategory()).isEqualTo(Category.THRILLER);
            assertThat(result.getDescription()).isNull();
            assertThat(result.getCast()).isNull();
        }
    }

    @Nested
    @DisplayName("toResponseList")
    class ToResponseList {

        @Test
        @DisplayName("returns empty list when entities is null")
        void returnsEmptyWhenNull() {
            List<VideoResponse> result = videoMapper.toResponseList(null);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty list when entities is empty")
        void returnsEmptyWhenEmpty() {
            List<VideoResponse> result = videoMapper.toResponseList(List.of());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("maps list of entities to list of responses")
        void mapsList() {
            Video entity2 = Video.builder()
                    .id(2L)
                    .title("Second")
                    .type(VideoType.SERIE)
                    .category(Category.HORREUR)
                    .build();

            List<VideoResponse> result = videoMapper.toResponseList(List.of(entity, entity2));

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getTitle()).isEqualTo("Test Film");
            assertThat(result.get(1).getId()).isEqualTo(2L);
            assertThat(result.get(1).getTitle()).isEqualTo("Second");
        }
    }
}
