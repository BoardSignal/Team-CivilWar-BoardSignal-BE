package com.civilwar.boardsignal.boardgame.domain.repository;

import com.civilwar.boardsignal.boardgame.domain.entity.Tip;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TipRepository {

    Optional<Tip> findByUserId(Long userId);

    Tip save(Tip tip);

    void saveAll(Collection<Tip> tips);

    List<Tip> findAllByBoardGameId(Long boardGameId);

    Optional<Tip> findById(Long id);

    Optional<Tip> findByIdWithLock(Long id);

}
