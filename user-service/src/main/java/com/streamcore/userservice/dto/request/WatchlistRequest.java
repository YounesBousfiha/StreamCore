package com.streamcore.userservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchlistRequest {

    @NotNull(message = "Video ID is required")
    private Long videoId;
}
