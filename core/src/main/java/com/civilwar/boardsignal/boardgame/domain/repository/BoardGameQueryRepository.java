package com.civilwar.boardsignal.boardgame.domain.repository;

import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.dto.request.BoardGameSearchCondition;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardGameQueryRepository {

    Optional<BoardGame> findById(Long id);

    Page<BoardGame> findAll(BoardGameSearchCondition condition, Pageable pageable);

}
