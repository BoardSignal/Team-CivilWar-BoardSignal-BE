package com.civilwar.boardsignal.boardgame.infrastructure.repository;

import com.civilwar.boardsignal.boardgame.domain.entity.Tip;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipJpaRepository extends JpaRepository<Tip, Long> {

    Optional<Tip> findByUserId(Long userId);

    List<Tip> findAllByBoardGameId(Long boardGameId);
}
