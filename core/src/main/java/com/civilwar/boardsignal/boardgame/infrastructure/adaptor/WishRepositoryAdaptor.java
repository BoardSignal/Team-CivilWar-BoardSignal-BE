package com.civilwar.boardsignal.boardgame.infrastructure.adaptor;

import com.civilwar.boardsignal.boardgame.domain.entity.Wish;
import com.civilwar.boardsignal.boardgame.domain.repository.WishRepository;
import com.civilwar.boardsignal.boardgame.infrastructure.repository.WishJpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WishRepositoryAdaptor implements WishRepository {

    private final WishJpaRepository wishJpaRepository;

    @Override
    public Optional<Wish> findByUserIdAndBoardGameId(Long userId, Long boardGameId) {
        return wishJpaRepository.findByUserIdAndBoardGameId(userId, boardGameId);
    }

    @Override
    public Wish save(Wish wish) {
        return wishJpaRepository.save(wish);
    }

    @Override
    public void saveAll(Collection<Wish> wishes) {
        wishJpaRepository.saveAll(wishes);
    }

    @Override
    public void deleteById(Long wishId) {
        wishJpaRepository.deleteById(wishId);
    }

    @Override
    public int countWishByUserId(Long userId) {
        return wishJpaRepository.countWishByUserId(userId);
    }

    @Override
    public List<Wish> findAllByUserId(Long userId) {
        return wishJpaRepository.findAllByUserId(userId);
    }
}
