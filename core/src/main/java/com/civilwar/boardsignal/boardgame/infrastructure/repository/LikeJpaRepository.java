package com.civilwar.boardsignal.boardgame.infrastructure.repository;

import com.civilwar.boardsignal.boardgame.domain.entity.Like;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {

    List<Like> findAllByTipId(Long tipId);

    void deleteByTipIdAndUserId(Long tipId, Long userId);
}
