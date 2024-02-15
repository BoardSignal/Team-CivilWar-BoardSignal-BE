package com.civilwar.boardsignal.boardgame.domain.repository;

import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import java.util.Collection;

public interface BoardGameRepository {

    BoardGame save(BoardGame boardGame);

    void saveAll(Collection<BoardGame> boardGames);

}
