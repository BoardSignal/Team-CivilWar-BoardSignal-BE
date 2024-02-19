package com.civilwar.boardsignal.boardgame.domain.repository;

import com.civilwar.boardsignal.boardgame.domain.entity.Tip;
import java.util.Collection;
import java.util.Optional;

public interface TipRepository {

    Optional<Tip> findByUserId(Long userId);

    Tip save(Tip tip);

    void saveAll(Collection<Tip> tips);

}
