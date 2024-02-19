package com.civilwar.boardsignal.boardgame.application;

import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.entity.Wish;
import com.civilwar.boardsignal.boardgame.domain.repository.BoardGameQueryRepository;
import com.civilwar.boardsignal.boardgame.domain.repository.WishRepository;
import com.civilwar.boardsignal.boardgame.dto.BoardGameMapper;
import com.civilwar.boardsignal.boardgame.dto.request.BoardGameSearchCondition;
import com.civilwar.boardsignal.boardgame.dto.response.BoardGamePageResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetAllBoardGamesResponse;
import com.civilwar.boardsignal.boardgame.dto.response.WishBoardGameResponse;
import com.civilwar.boardsignal.boardgame.exception.BoardGameErrorCode;
import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardGameService {

    private final BoardGameQueryRepository boardGameQueryRepository;
    private final WishRepository wishRepository;

    public BoardGamePageResponse<GetAllBoardGamesResponse> getAllBoardGames(
        BoardGameSearchCondition condition, Pageable pageable) {

        Slice<BoardGame> boardGames = boardGameQueryRepository.findAll(condition, pageable);
        Slice<GetAllBoardGamesResponse> findBoardGames = boardGames.map(
            BoardGameMapper::toGetAllBoardGamesResponse); // 응답 dto 형식으로 변환

        return BoardGameMapper.toBoardGamePageRepsonse(findBoardGames); // 커스텀 페이징 응답 dto에 담음
    }

    public WishBoardGameResponse wishBoardGame(User user, Long boardGameId) {
        BoardGame boardGame = boardGameQueryRepository.findById(boardGameId)
            .orElseThrow(
                () -> new NotFoundException(BoardGameErrorCode.NOT_FOUND_BOARD_GAME)
            );

        wishRepository.findByUserIdAndBoardGameId(user.getId(), boardGameId)
            .ifPresentOrElse(
                wish -> { // 찜한 내역이 있던 게임이라면 찜 취소 로직
                    boardGame.decreaseWishCount();
                    wishRepository.deleteById(wish.getId());
                },
                () -> { // 찜한 내역이 없던 게임이라면 찜 등록 로직
                    boardGame.increaseWishCount();
                    Wish wish = Wish.of(user.getId(), boardGameId);
                    wishRepository.save(wish);
                }
            );
        return new WishBoardGameResponse(boardGame.getWishCount());
    }
}
