package com.streamcore.userservice.controller;

import com.streamcore.userservice.dto.request.WatchHistoryRequest;
import com.streamcore.userservice.dto.response.WatchHistoryResponse;
import com.streamcore.userservice.service.WatchHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/history")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WatchHistoryController {

    private final WatchHistoryService watchHistoryService;

    @GetMapping
    public ResponseEntity<List<WatchHistoryResponse>> getWatchHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(watchHistoryService.findByUserId(userId, limit));
    }

    @PostMapping
    public ResponseEntity<WatchHistoryResponse> logWatchHistory(
            @PathVariable Long userId,
            @Valid @RequestBody WatchHistoryRequest request) {
        WatchHistoryResponse response = watchHistoryService.logWatchHistory(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
