package com.streamcore.videoservice.controller;

import com.streamcore.videoservice.dto.request.VideoRequest;
import com.streamcore.videoservice.dto.response.VideoResponse;
import com.streamcore.videoservice.enums.Category;
import com.streamcore.videoservice.enums.VideoType;
import com.streamcore.videoservice.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VideoController {

    private final VideoService videoService;

    @GetMapping
    public ResponseEntity<List<VideoResponse>> getAllVideos() {
        return ResponseEntity.ok(videoService.findAll());
    }

    @GetMapping("/batch")
    public ResponseEntity<List<VideoResponse>> getVideosByIds(@RequestParam("ids") List<Long> ids) {
        return ResponseEntity.ok(videoService.findByIds(ids));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VideoResponse> getVideoById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<VideoResponse> createVideo(@Valid @RequestBody VideoRequest request) {
        VideoResponse response = videoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VideoResponse> updateVideo(
            @PathVariable Long id,
            @Valid @RequestBody VideoRequest request) {
        return ResponseEntity.ok(videoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVideo(@PathVariable Long id) {
        videoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<VideoResponse>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(videoService.findByTitleContaining(title));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<VideoResponse>> getByType(@PathVariable VideoType type) {
        return ResponseEntity.ok(videoService.findByType(type));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<VideoResponse>> getByCategory(@PathVariable Category category) {
        return ResponseEntity.ok(videoService.findByCategory(category));
    }
}
