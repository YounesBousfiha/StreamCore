package com.streamcore.userservice.client.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoResponseDto {

    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String trailerUrl;
    private Integer duration;
    private Integer releaseYear;
    private String type;
    private String category;
    private Double rating;
    private String director;
    private List<String> cast;
}
