package com.streamcore.videoservice.repository;

import com.streamcore.videoservice.entity.Video;
import com.streamcore.videoservice.enums.Category;
import com.streamcore.videoservice.enums.VideoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByType(VideoType type);

    List<Video> findByCategory(Category category);

    List<Video> findByTypeAndCategory(VideoType type, Category category);

    List<Video> findByTitleContainingIgnoreCase(String title);
}
