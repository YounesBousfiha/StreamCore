package com.streamcore.videoservice.entity;

import com.streamcore.videoservice.enums.Category;
import com.streamcore.videoservice.enums.VideoType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String thumbnailUrl;
    private String trailerUrl;
    private Integer duration; // in minutes

    private Integer releaseYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    private Double rating;
    private String director;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "video_cast", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "cast_member")
    private List<String> cast;
}
