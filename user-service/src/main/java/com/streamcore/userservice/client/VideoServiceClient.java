package com.streamcore.userservice.client;

import com.streamcore.userservice.client.dto.VideoResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "video-service", fallback = VideoServiceClientFallback.class)
public interface VideoServiceClient {

    @GetMapping("/api/videos/{id}")
    VideoResponseDto getVideoById(@PathVariable("id") Long id);

    @GetMapping("/api/videos/batch")
    List<VideoResponseDto> getVideosByIds(@RequestParam("ids") List<Long> ids);
}
