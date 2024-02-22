package com.civilwar.boardsignal.boardgame.domain.repository;

import com.civilwar.boardsignal.boardgame.domain.entity.Wish;
import java.util.Collection;
import java.util.Optional;

public interface WishRepository {

    Optional<Wish> findByUserIdAndBoardGameId(Long userId, Long boardGameId);

    Wish save(Wish wish);

    void saveAll(Collection<Wish> wishes);

    void deleteById(Long wishId);

    int countWishByUserId(Long userId);
}
