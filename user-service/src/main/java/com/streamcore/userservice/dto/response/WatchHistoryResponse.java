package com.streamcore.userservice.dto.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchHistoryResponse {

    private Long id;
    private Long userId;
    private Long videoId;
    private Instant watchedAt;
    private Integer progressTime;
    private Boolean completed;
}
