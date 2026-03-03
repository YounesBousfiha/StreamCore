package com.streamcore.userservice.repository;

import com.streamcore.userservice.entity.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findByUserIdOrderByAddedAtDesc(Long userId);

    Optional<Watchlist> findByUserIdAndVideoId(Long userId, Long videoId);

    boolean existsByUserIdAndVideoId(Long userId, Long videoId);

    void deleteByUserIdAndVideoId(Long userId, Long videoId);
}
