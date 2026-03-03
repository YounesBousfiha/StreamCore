package com.streamcore.userservice.dto.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchlistResponse {

    private Long id;
    private Long userId;
    private Long videoId;
    private Instant addedAt;
}
