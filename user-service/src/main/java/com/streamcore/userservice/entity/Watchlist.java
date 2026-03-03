package com.streamcore.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "watchlists", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "video_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;
}
