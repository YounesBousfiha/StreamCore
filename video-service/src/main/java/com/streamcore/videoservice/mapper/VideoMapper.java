package com.streamcore.videoservice.mapper;

import com.streamcore.videoservice.dto.request.VideoRequest;
import com.streamcore.videoservice.dto.response.VideoResponse;
import com.streamcore.videoservice.entity.Video;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VideoMapper {

    public Video toEntity(VideoRequest request) {
        if (request == null) return null;
        return Video.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .trailerUrl(request.getTrailerUrl())
                .duration(request.getDuration())
                .releaseYear(request.getReleaseYear())
                .type(request.getType())
                .category(request.getCategory())
                .rating(request.getRating())
                .director(request.getDirector())
                .cast(request.getCast())
                .build();
    }

    public void updateEntity(Video entity, VideoRequest request) {
        if (entity == null || request == null) return;
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setThumbnailUrl(request.getThumbnailUrl());
        entity.setTrailerUrl(request.getTrailerUrl());
        entity.setDuration(request.getDuration());
        entity.setReleaseYear(request.getReleaseYear());
        entity.setType(request.getType());
        entity.setCategory(request.getCategory());
        entity.setRating(request.getRating());
        entity.setDirector(request.getDirector());
        entity.setCast(request.getCast());
    }

    public VideoResponse toResponse(Video entity) {
        if (entity == null) return null;
        return VideoResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .thumbnailUrl(entity.getThumbnailUrl())
                .trailerUrl(entity.getTrailerUrl())
                .duration(entity.getDuration())
                .releaseYear(entity.getReleaseYear())
                .type(entity.getType())
                .category(entity.getCategory())
                .rating(entity.getRating())
                .director(entity.getDirector())
                .cast(entity.getCast())
                .build();
    }

    public List<VideoResponse> toResponseList(List<Video> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
