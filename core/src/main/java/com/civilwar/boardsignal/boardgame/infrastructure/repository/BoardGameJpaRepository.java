package com.civilwar.boardsignal.boardgame.infrastructure.repository;

import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardGameJpaRepository extends JpaRepository<BoardGame, Long> {

}
