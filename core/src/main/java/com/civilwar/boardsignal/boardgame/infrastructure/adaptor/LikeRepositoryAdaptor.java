package com.civilwar.boardsignal.boardgame.infrastructure.adaptor;

import com.civilwar.boardsignal.boardgame.domain.entity.Like;
import com.civilwar.boardsignal.boardgame.domain.repository.LikeRepository;
import com.civilwar.boardsignal.boardgame.infrastructure.repository.LikeJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryAdaptor implements LikeRepository {

    private final LikeJpaRepository likeJpaRepository;

    @Override
    public List<Like> findAll() {
        return likeJpaRepository.findAll();
    }

    @Override
    public Optional<Like> findById(Long id) {
        return likeJpaRepository.findById(id);
    }

    @Override
    public Like save(Like like) {
        return likeJpaRepository.save(like);
    }

    @Override
    public void deleteByTipIdAndUserId(Long tipId, Long userId) {
        likeJpaRepository.deleteByTipIdAndUserId(tipId, userId);
    }

    @Override
    public List<Like> findAllByUserId(Long userId) {
        return likeJpaRepository.findAllByUserId(userId);
    }
}
