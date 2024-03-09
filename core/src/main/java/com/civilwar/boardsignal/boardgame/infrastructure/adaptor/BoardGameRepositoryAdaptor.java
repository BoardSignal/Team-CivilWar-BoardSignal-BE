package com.civilwar.boardsignal.boardgame.infrastructure.adaptor;

import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.repository.BoardGameRepository;
import com.civilwar.boardsignal.boardgame.infrastructure.repository.BoardGameJpaRepository;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardGameRepositoryAdaptor implements BoardGameRepository {

    private final BoardGameJpaRepository boardGameJpaRepository;

    @Override
    public BoardGame save(BoardGame boardGame) {
        return boardGameJpaRepository.save(boardGame);
    }

    @Override
    public void saveAll(Collection<BoardGame> boardGames) {
        boardGameJpaRepository.saveAll(boardGames);
    }
}
