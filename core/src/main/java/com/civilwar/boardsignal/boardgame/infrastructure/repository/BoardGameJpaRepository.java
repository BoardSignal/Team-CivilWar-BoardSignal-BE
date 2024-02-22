package com.civilwar.boardsignal.boardgame.infrastructure.repository;

import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardGameJpaRepository extends JpaRepository<BoardGame, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from BoardGame b where b.id = :id")
    Optional<BoardGame> findByIdWithLock(@Param("id") Long id);
}
