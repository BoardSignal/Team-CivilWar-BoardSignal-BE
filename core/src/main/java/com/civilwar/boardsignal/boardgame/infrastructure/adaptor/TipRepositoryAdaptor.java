package com.civilwar.boardsignal.boardgame.infrastructure.adaptor;

import com.civilwar.boardsignal.boardgame.domain.entity.Tip;
import com.civilwar.boardsignal.boardgame.domain.repository.TipRepository;
import com.civilwar.boardsignal.boardgame.infrastructure.repository.TipJpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TipRepositoryAdaptor implements TipRepository {

    private final TipJpaRepository tipJpaRepository;

    @Override
    public Optional<Tip> findByBoardGameIdAndUserId(Long boardGameId, Long userId) {
        return tipJpaRepository.findByBoardGameIdAndUserId(boardGameId, userId);
    }

    @Override
    public Tip save(Tip tip) {
        return tipJpaRepository.save(tip);
    }

    @Override
    public void saveAll(Collection<Tip> tips) {
        tipJpaRepository.saveAll(tips);
    }

    @Override
    public List<Tip> findAllByBoardGameId(Long boardGameId) {
        return tipJpaRepository.findAllByBoardGameId(boardGameId);
    }

    @Override
    public Optional<Tip> findById(Long id) {
        return tipJpaRepository.findById(id);
    }

    @Override
    public Optional<Tip> findByIdWithLock(Long id) {
        return tipJpaRepository.findByIdWithLock(id);
    }


    @Override
    public void deleteByTipIdAndUserId(Long tipId, Long userId) {
        tipJpaRepository.deleteByIdAndUserId(tipId, userId);
    }
}
