package com.streamcore.videoservice.dto.response;

import com.streamcore.videoservice.enums.Category;
import com.streamcore.videoservice.enums.VideoType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoResponse {

    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String trailerUrl;
    private Integer duration;
    private Integer releaseYear;
    private VideoType type;
    private Category category;
    private Double rating;
    private String director;
    private List<String> cast;
}
