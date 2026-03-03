package com.streamcore.videoservice.dto.request;

import com.streamcore.videoservice.enums.Category;
import com.streamcore.videoservice.enums.VideoType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @Size(max = 2000)
    private String description;

    private String thumbnailUrl;
    private String trailerUrl;

    @Min(0)
    private Integer duration;

    @Min(1900)
    @Max(2100)
    private Integer releaseYear;

    @NotNull(message = "Type is required")
    private VideoType type;

    @NotNull(message = "Category is required")
    private Category category;

    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private Double rating;

    private String director;
    private List<String> cast;
}
