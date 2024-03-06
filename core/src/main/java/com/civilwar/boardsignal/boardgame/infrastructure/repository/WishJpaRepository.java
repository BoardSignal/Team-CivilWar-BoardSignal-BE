package com.civilwar.boardsignal.boardgame.infrastructure.repository;

import com.civilwar.boardsignal.boardgame.domain.entity.Wish;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishJpaRepository extends JpaRepository<Wish, Long> {

    Optional<Wish> findByUserIdAndBoardGameId(Long userId, Long boardGameId);

    int countWishByUserId(Long userId);

    List<Wish> findAllByUserId(Long userId);
}
