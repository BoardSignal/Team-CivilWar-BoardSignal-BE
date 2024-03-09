package com.civilwar.boardsignal.boardgame.domain.repository;

import com.civilwar.boardsignal.boardgame.domain.entity.Like;
import java.util.List;
import java.util.Optional;

public interface LikeRepository {

    List<Like> findAll();

    Optional<Like> findById(Long id);

    Like save(Like like);

    void deleteByTipIdAndUserId(Long tipId, Long userId);

    List<Like> findAllByUserId(Long userId);
}
