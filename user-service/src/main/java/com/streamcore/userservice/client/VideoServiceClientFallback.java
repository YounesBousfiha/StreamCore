package com.streamcore.userservice.client;

import com.streamcore.userservice.client.dto.VideoResponseDto;
import com.streamcore.userservice.exception.UserVideoServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class VideoServiceClientFallback implements VideoServiceClient {

    @Override
    public VideoResponseDto getVideoById(Long id) {
        log.warn("VideoServiceClient fallback triggered for videoId={}. Video service is unavailable.", id);
        throw new UserVideoServiceUnavailableException("Video service is temporarily unavailable. Please try again later.");
    }

    @Override
    public List<VideoResponseDto> getVideosByIds(List<Long> ids) {
        log.warn("VideoServiceClient fallback triggered for batch ids={}. Video service is unavailable.", ids);
        return Collections.emptyList();
    }
}
