package com.streamcore.userservice.repository;

import com.streamcore.userservice.entity.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {

    List<WatchHistory> findByUserIdOrderByWatchedAtDesc(Long userId, org.springframework.data.domain.Pageable pageable);

    List<WatchHistory> findByUserId(Long userId);
}
