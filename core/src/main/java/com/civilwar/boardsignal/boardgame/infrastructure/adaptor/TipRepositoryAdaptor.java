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
    public Optional<Tip> findByUserId(Long userId) {
        return tipJpaRepository.findByUserId(userId);
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
}
