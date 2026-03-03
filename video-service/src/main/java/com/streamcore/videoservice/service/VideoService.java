package com.streamcore.videoservice.service;

import com.streamcore.videoservice.dto.request.VideoRequest;
import com.streamcore.videoservice.dto.response.VideoResponse;
import com.streamcore.videoservice.entity.Video;
import com.streamcore.videoservice.enums.Category;
import com.streamcore.videoservice.enums.VideoType;
import com.streamcore.videoservice.exception.VideoResourceNotFoundException;
import com.streamcore.videoservice.mapper.VideoMapper;
import com.streamcore.videoservice.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;

    public List<VideoResponse> findAll() {
        return videoMapper.toResponseList(videoRepository.findAll());
    }

    public VideoResponse findById(Long id) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new VideoResourceNotFoundException("Video", "id", id));
        return videoMapper.toResponse(video);
    }

    public List<VideoResponse> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return videoMapper.toResponseList(videoRepository.findAllById(ids));
    }

    @Transactional
    public VideoResponse create(VideoRequest request) {
        Video video = videoMapper.toEntity(request);
        video = videoRepository.save(video);
        return videoMapper.toResponse(video);
    }

    @Transactional
    public VideoResponse update(Long id, VideoRequest request) {
        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new VideoResourceNotFoundException("Video", "id", id));
        videoMapper.updateEntity(video, request);
        video = videoRepository.save(video);
        return videoMapper.toResponse(video);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!videoRepository.existsById(id)) {
            throw new VideoResourceNotFoundException("Video", "id", id);
        }
        videoRepository.deleteById(id);
    }

    public List<VideoResponse> findByType(VideoType type) {
        return videoMapper.toResponseList(videoRepository.findByType(type));
    }

    public List<VideoResponse> findByCategory(Category category) {
        return videoMapper.toResponseList(videoRepository.findByCategory(category));
    }

    public List<VideoResponse> findByTitleContaining(String title) {
        return videoMapper.toResponseList(videoRepository.findByTitleContainingIgnoreCase(title));
    }
}
