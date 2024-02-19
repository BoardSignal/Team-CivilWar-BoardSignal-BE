package com.civilwar.boardsignal.boardgame.domain.repository;

import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.dto.request.BoardGameSearchCondition;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface BoardGameQueryRepository {

    Optional<BoardGame> findById(Long id);

    Slice<BoardGame> findAll(BoardGameSearchCondition condition, Pageable pageable);

}
