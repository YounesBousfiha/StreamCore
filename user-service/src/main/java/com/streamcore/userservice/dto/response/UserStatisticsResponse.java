package com.streamcore.userservice.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatisticsResponse {

    private Long totalVideosWatched;
    private Long totalWatchTimeSeconds;
    private String mostWatchedCategory;
}
