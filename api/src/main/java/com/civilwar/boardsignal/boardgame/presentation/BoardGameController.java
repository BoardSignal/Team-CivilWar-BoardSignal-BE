package com.civilwar.boardsignal.boardgame.presentation;

import static com.civilwar.boardsignal.boardgame.dto.mapper.BoardGameApiMapper.toAddTipRequest;

import com.civilwar.boardsignal.boardgame.application.BoardGameService;
import com.civilwar.boardsignal.boardgame.dto.request.AddTipRequest;
import com.civilwar.boardsignal.boardgame.dto.request.ApiAddTipRequest;
import com.civilwar.boardsignal.boardgame.dto.request.BoardGameSearchCondition;
import com.civilwar.boardsignal.boardgame.dto.response.AddTipResposne;
import com.civilwar.boardsignal.boardgame.dto.response.BoardGamePageResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetAllBoardGamesResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetBoardGameResponse;
import com.civilwar.boardsignal.boardgame.dto.response.LikeTipResponse;
import com.civilwar.boardsignal.boardgame.dto.response.WishBoardGameResponse;
import com.civilwar.boardsignal.user.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Board Game API")
@RestController
@RequestMapping("/api/v1/board-games")
@RequiredArgsConstructor
public class BoardGameController {

    private final BoardGameService boardGameService;

    @Operation(summary = "보드게임 목록 조회 API")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping
    public ResponseEntity<BoardGamePageResponse<GetAllBoardGamesResponse>> getAllBoardGames(
        BoardGameSearchCondition condition,
        Pageable pageable
    ) {
        BoardGamePageResponse<GetAllBoardGamesResponse> boardGames = boardGameService.getAllBoardGames(
            condition, pageable);
        return ResponseEntity.ok(boardGames);
    }

    @Operation(summary = "보드게임 찜하기 API")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/wish/{boardGameId}")
    public ResponseEntity<WishBoardGameResponse> wishBoardGame(
        @Parameter(hidden = true)
        @AuthenticationPrincipal User user,
        @PathVariable("boardGameId") Long boardGameId
    ) {
        WishBoardGameResponse response = boardGameService.wishBoardGame(user, boardGameId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "보드게임 공략 등록 API")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/tip/{boardGameId}")
    public ResponseEntity<AddTipResposne> addTip(
        @Parameter(hidden = true)
        @AuthenticationPrincipal User user,
        @PathVariable("boardGameId") Long boardGameId,
        @RequestBody ApiAddTipRequest request
    ) {
        AddTipRequest addTipRequest = toAddTipRequest(request);
        AddTipResposne response = boardGameService.addTip(user, boardGameId, addTipRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "보드게임 상세정보 조회 API")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/{boardGameId}")
    public ResponseEntity<GetBoardGameResponse> getBoard(
        @PathVariable("boardGameId") Long boardGameId
    ) {
        GetBoardGameResponse response = boardGameService.getBoardGame(boardGameId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "보드게임 공략 좋아요 API")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/like/{tipId}")
    public ResponseEntity<LikeTipResponse> likeTip(
        @Parameter(hidden = true)
        @AuthenticationPrincipal User user,
        @PathVariable("tipId") Long tipId
    ) {
        LikeTipResponse response = boardGameService.likeTip(user, tipId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "보드게임 공략 좋아요 취소 API")
    @ApiResponse(useReturnTypeSchema = true)
    @DeleteMapping("/like/{tipId}")
    public ResponseEntity<LikeTipResponse> cancelLikeTip(
        @Parameter(hidden = true)
        @AuthenticationPrincipal User user,
        @PathVariable("tipId") Long tipId
    ) {
        LikeTipResponse response = boardGameService.cancelLikeTip(user, tipId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "보드게임 공략 삭제 API")
    @ApiResponse(useReturnTypeSchema = true)
    @DeleteMapping("/{tipId}")
    public void deleteTip(
        @Parameter(hidden = true)
        @AuthenticationPrincipal User user,
        @PathVariable("tipId") Long tipId
    ) {
        boardGameService.deleteTip(user, tipId);
    }

}
