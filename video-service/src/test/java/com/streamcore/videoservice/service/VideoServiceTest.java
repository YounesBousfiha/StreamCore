package com.streamcore.videoservice.service;

import com.streamcore.videoservice.dto.request.VideoRequest;
import com.streamcore.videoservice.dto.response.VideoResponse;
import com.streamcore.videoservice.entity.Video;
import com.streamcore.videoservice.enums.Category;
import com.streamcore.videoservice.enums.VideoType;
import com.streamcore.videoservice.exception.VideoResourceNotFoundException;
import com.streamcore.videoservice.mapper.VideoMapper;
import com.streamcore.videoservice.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VideoService")
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private VideoMapper videoMapper;

    @InjectMocks
    private VideoService videoService;

    private Video entity;
    private VideoResponse response;
    private VideoRequest request;

    @BeforeEach
    void setUp() {
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
                .cast(List.of("Actor1"))
                .build();

        response = VideoResponse.builder()
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
                .cast(List.of("Actor1"))
                .build();

        request = VideoRequest.builder()
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
                .cast(List.of("Actor1"))
                .build();
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("returns list from mapper when repository returns entities")
        void returnsMappedList() {
            List<Video> entities = List.of(entity);
            List<VideoResponse> responses = List.of(response);

            when(videoRepository.findAll()).thenReturn(entities);
            when(videoMapper.toResponseList(entities)).thenReturn(responses);

            List<VideoResponse> result = videoService.findAll();

            assertThat(result).isSameAs(responses);
            verify(videoRepository).findAll();
            verify(videoMapper).toResponseList(entities);
        }

        @Test
        @DisplayName("returns empty list when repository returns empty")
        void returnsEmptyWhenNoData() {
            when(videoRepository.findAll()).thenReturn(List.of());
            when(videoMapper.toResponseList(List.of())).thenReturn(List.of());

            List<VideoResponse> result = videoService.findAll();

            assertThat(result).isEmpty();
            verify(videoMapper).toResponseList(List.of());
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("returns mapped response when video exists")
        void returnsResponseWhenFound() {
            when(videoRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(videoMapper.toResponse(entity)).thenReturn(response);

            VideoResponse result = videoService.findById(1L);

            assertThat(result).isSameAs(response);
            verify(videoRepository).findById(1L);
            verify(videoMapper).toResponse(entity);
        }

        @Test
        @DisplayName("throws VideoResourceNotFoundException when video not found")
        void throwsWhenNotFound() {
            when(videoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> videoService.findById(999L))
                    .isInstanceOf(VideoResourceNotFoundException.class)
                    .hasMessageContaining("Video")
                    .hasMessageContaining("id")
                    .hasMessageContaining("999");

            verify(videoRepository).findById(999L);
            verify(videoMapper, never()).toResponse(any());
        }
    }

    @Nested
    @DisplayName("findByIds")
    class FindByIds {

        @Test
        @DisplayName("returns empty list when ids is null")
        void returnsEmptyWhenIdsNull() {
            List<VideoResponse> result = videoService.findByIds(null);

            assertThat(result).isEmpty();
            verify(videoRepository, never()).findAllById(any());
            verify(videoMapper, never()).toResponseList(any());
        }

        @Test
        @DisplayName("returns empty list when ids is empty")
        void returnsEmptyWhenIdsEmpty() {
            List<VideoResponse> result = videoService.findByIds(List.of());

            assertThat(result).isEmpty();
            verify(videoRepository, never()).findAllById(any());
            verify(videoMapper, never()).toResponseList(any());
        }

        @Test
        @DisplayName("returns mapped list when ids provided")
        void returnsMappedListWhenIdsProvided() {
            List<Long> ids = List.of(1L, 2L);
            List<Video> entities = List.of(entity);
            List<VideoResponse> responses = List.of(response);

            when(videoRepository.findAllById(ids)).thenReturn(entities);
            when(videoMapper.toResponseList(entities)).thenReturn(responses);

            List<VideoResponse> result = videoService.findByIds(ids);

            assertThat(result).isSameAs(responses);
            verify(videoRepository).findAllById(ids);
            verify(videoMapper).toResponseList(entities);
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("maps request to entity, saves, and returns mapped response")
        void createsVideo() {
            when(videoMapper.toEntity(request)).thenReturn(entity);
            when(videoRepository.save(entity)).thenReturn(entity);
            when(videoMapper.toResponse(entity)).thenReturn(response);

            VideoResponse result = videoService.create(request);

            assertThat(result).isSameAs(response);
            verify(videoMapper).toEntity(request);
            verify(videoRepository).save(entity);
            verify(videoMapper).toResponse(entity);
        }

        @Test
        @DisplayName("uses saved entity for response mapping")
        void usesSavedEntityForResponse() {
            Video savedEntity = Video.builder().id(2L).title("Saved").build();
            VideoResponse savedResponse = VideoResponse.builder().id(2L).title("Saved").build();

            when(videoMapper.toEntity(request)).thenReturn(entity);
            when(videoRepository.save(entity)).thenReturn(savedEntity);
            when(videoMapper.toResponse(savedEntity)).thenReturn(savedResponse);

            VideoResponse result = videoService.create(request);

            assertThat(result.getId()).isEqualTo(2L);
            verify(videoMapper).toResponse(savedEntity);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("updates entity and returns mapped response when found")
        void updatesWhenFound() {
            when(videoRepository.findById(1L)).thenReturn(Optional.of(entity));
            when(videoRepository.save(entity)).thenReturn(entity);
            when(videoMapper.toResponse(entity)).thenReturn(response);

            VideoResponse result = videoService.update(1L, request);

            assertThat(result).isSameAs(response);
            verify(videoRepository).findById(1L);
            verify(videoMapper).updateEntity(entity, request);
            verify(videoRepository).save(entity);
            verify(videoMapper).toResponse(entity);
        }

        @Test
        @DisplayName("throws VideoResourceNotFoundException when video not found")
        void throwsWhenNotFound() {
            when(videoRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> videoService.update(999L, request))
                    .isInstanceOf(VideoResourceNotFoundException.class)
                    .hasMessageContaining("999");

            verify(videoRepository).findById(999L);
            verify(videoMapper, never()).updateEntity(any(), any());
            verify(videoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteById")
    class DeleteById {

        @Test
        @DisplayName("deletes when video exists")
        void deletesWhenExists() {
            when(videoRepository.existsById(1L)).thenReturn(true);
            doNothing().when(videoRepository).deleteById(1L);

            videoService.deleteById(1L);

            verify(videoRepository).existsById(1L);
            verify(videoRepository).deleteById(1L);
        }

        @Test
        @DisplayName("throws VideoResourceNotFoundException when video does not exist")
        void throwsWhenNotExists() {
            when(videoRepository.existsById(999L)).thenReturn(false);

            assertThatThrownBy(() -> videoService.deleteById(999L))
                    .isInstanceOf(VideoResourceNotFoundException.class)
                    .hasMessageContaining("999");

            verify(videoRepository).existsById(999L);
            verify(videoRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("findByType")
    class FindByType {

        @Test
        @DisplayName("returns mapped list from repository by type")
        void returnsMappedList() {
            List<Video> entities = List.of(entity);
            List<VideoResponse> responses = List.of(response);

            when(videoRepository.findByType(VideoType.FILM)).thenReturn(entities);
            when(videoMapper.toResponseList(entities)).thenReturn(responses);

            List<VideoResponse> result = videoService.findByType(VideoType.FILM);

            assertThat(result).isSameAs(responses);
            verify(videoRepository).findByType(VideoType.FILM);
            verify(videoMapper).toResponseList(entities);
        }
    }

    @Nested
    @DisplayName("findByCategory")
    class FindByCategory {

        @Test
        @DisplayName("returns mapped list from repository by category")
        void returnsMappedList() {
            List<Video> entities = List.of(entity);
            List<VideoResponse> responses = List.of(response);

            when(videoRepository.findByCategory(Category.ACTION)).thenReturn(entities);
            when(videoMapper.toResponseList(entities)).thenReturn(responses);

            List<VideoResponse> result = videoService.findByCategory(Category.ACTION);

            assertThat(result).isSameAs(responses);
            verify(videoRepository).findByCategory(Category.ACTION);
            verify(videoMapper).toResponseList(entities);
        }
    }

    @Nested
    @DisplayName("findByTitleContaining")
    class FindByTitleContaining {

        @Test
        @DisplayName("returns mapped list from repository by title")
        void returnsMappedList() {
            List<Video> entities = List.of(entity);
            List<VideoResponse> responses = List.of(response);

            when(videoRepository.findByTitleContainingIgnoreCase("Test")).thenReturn(entities);
            when(videoMapper.toResponseList(entities)).thenReturn(responses);

            List<VideoResponse> result = videoService.findByTitleContaining("Test");

            assertThat(result).isSameAs(responses);
            verify(videoRepository).findByTitleContainingIgnoreCase("Test");
            verify(videoMapper).toResponseList(entities);
        }
    }
}
