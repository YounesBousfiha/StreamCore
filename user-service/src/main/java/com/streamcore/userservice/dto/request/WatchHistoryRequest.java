package com.streamcore.userservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchHistoryRequest {

    @NotNull(message = "Video ID is required")
    private Long videoId;

    private Integer progressTime; // in seconds

    @NotNull(message = "Completed flag is required")
    private Boolean completed;
}
