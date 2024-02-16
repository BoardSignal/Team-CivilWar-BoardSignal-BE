package com.civilwar.boardsignal.boardgame.presentation;

import com.civilwar.boardsignal.boardgame.application.BoardGameService;
import com.civilwar.boardsignal.boardgame.dto.request.BoardGameSearchCondition;
import com.civilwar.boardsignal.boardgame.dto.response.BoardGamePageResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetAllBoardGamesResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Board Game API")
@RestController
@RequestMapping("/api/v1/board-games")
@RequiredArgsConstructor
public class BoardGameController {

    private final BoardGameService boardGameService;

    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping
    public ResponseEntity<BoardGamePageResponse<GetAllBoardGamesResponse>> getAllBoardGames(
        @Parameter(hidden = true) BoardGameSearchCondition condition,
        Pageable pageable
    ) {
        BoardGamePageResponse<GetAllBoardGamesResponse> boardGames = boardGameService.getAllBoardGames(
            condition, pageable);
        return ResponseEntity.ok(boardGames);
    }
}
