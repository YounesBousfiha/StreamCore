package com.streamcore.userservice.controller;

import com.streamcore.userservice.dto.request.WatchlistRequest;
import com.streamcore.userservice.dto.response.WatchlistResponse;
import com.streamcore.userservice.service.WatchlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/watchlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WatchlistController {

    private final WatchlistService watchlistService;

    @GetMapping
    public ResponseEntity<List<WatchlistResponse>> getWatchlist(@PathVariable Long userId) {
        return ResponseEntity.ok(watchlistService.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<WatchlistResponse> addToWatchlist(
            @PathVariable Long userId,
            @Valid @RequestBody WatchlistRequest request) {
        WatchlistResponse response = watchlistService.addToWatchlist(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<Void> removeFromWatchlist(
            @PathVariable Long userId,
            @PathVariable Long videoId) {
        watchlistService.removeFromWatchlist(userId, videoId);
        return ResponseEntity.noContent().build();
    }
}
