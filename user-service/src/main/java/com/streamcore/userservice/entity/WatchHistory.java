package com.streamcore.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "watch_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "watched_at", nullable = false)
    private Instant watchedAt;

    @Column(name = "progress_time")
    private Integer progressTime; // in seconds

    @Column(nullable = false)
    private Boolean completed;
}
