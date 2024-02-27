package com.civilwar.boardsignal.boardgame.infrastructure.repository;

import com.civilwar.boardsignal.boardgame.domain.entity.Tip;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TipJpaRepository extends JpaRepository<Tip, Long> {

    Optional<Tip> findByBoardGameIdAndUserId(Long boardGameId, Long userId);

    @Query("select t from Tip t where t.boardGameId = :boardGameId order by t.likeCount desc ")
    List<Tip> findAllByBoardGameId(Long boardGameId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Tip t where t.id = :id")
    Optional<Tip> findByIdWithLock(@Param("id") Long id);

    void deleteByIdAndUserId(Long id, Long userId);
}
