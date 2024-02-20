package com.civilwar.boardsignal.boardgame.application;

import static com.civilwar.boardsignal.boardgame.exception.BoardGameErrorCode.AlREADY_TIP_ADDED;
import static com.civilwar.boardsignal.boardgame.exception.BoardGameErrorCode.NOT_FOUND_BOARD_GAME;

import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.entity.Tip;
import com.civilwar.boardsignal.boardgame.domain.entity.Wish;
import com.civilwar.boardsignal.boardgame.domain.repository.BoardGameQueryRepository;
import com.civilwar.boardsignal.boardgame.domain.repository.TipRepository;
import com.civilwar.boardsignal.boardgame.domain.repository.WishRepository;
import com.civilwar.boardsignal.boardgame.dto.BoardGameMapper;
import com.civilwar.boardsignal.boardgame.dto.request.AddTipRequest;
import com.civilwar.boardsignal.boardgame.dto.request.BoardGameSearchCondition;
import com.civilwar.boardsignal.boardgame.dto.response.AddTipResposne;
import com.civilwar.boardsignal.boardgame.dto.response.BoardGamePageResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetAllBoardGamesResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetBoardGameResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetTipResposne;
import com.civilwar.boardsignal.boardgame.dto.response.WishBoardGameResponse;
import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.common.exception.ValidationException;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardGameService {

    private final BoardGameQueryRepository boardGameQueryRepository;
    private final WishRepository wishRepository;
    private final TipRepository tipRepository;
    private final UserRepository userRepository;

    private void validateTipExists(User user) { // 이미 공략을 등록한 적이 있는 지 검증
        tipRepository.findByUserId(user.getId())
            .ifPresent(tip -> {
                throw new ValidationException(AlREADY_TIP_ADDED);
            });
    }

    public BoardGamePageResponse<GetAllBoardGamesResponse> getAllBoardGames(
        BoardGameSearchCondition condition, Pageable pageable) {

        Slice<BoardGame> boardGames = boardGameQueryRepository.findAll(condition, pageable);
        Slice<GetAllBoardGamesResponse> findBoardGames = boardGames.map(
            BoardGameMapper::toGetAllBoardGamesResponse); // 응답 dto 형식으로 변환

        return BoardGameMapper.toBoardGamePageRepsonse(findBoardGames); // 커스텀 페이징 응답 dto에 담음
    }

    @Transactional
    public WishBoardGameResponse wishBoardGame(User user, Long boardGameId) {
        BoardGame boardGame = boardGameQueryRepository.findById(boardGameId)
            .orElseThrow(
                () -> new NotFoundException(NOT_FOUND_BOARD_GAME)
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

    @Transactional
    public AddTipResposne addTip(
        User user,
        Long boardGameId,
        AddTipRequest request
    ) {
        validateTipExists(user);

        Tip tip = Tip.of(boardGameId, user.getId(), request.content());

        Tip savedTip = tipRepository.save(tip);

        return new AddTipResposne(savedTip.getContent());
    }

    @Transactional(readOnly = true)
    public GetBoardGameResponse getBoardGame(Long boardGameId) {
        BoardGame boardGame = boardGameQueryRepository.findById(boardGameId)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_BOARD_GAME));

        List<Tip> tips = tipRepository.findAllByBoardGameId(boardGameId);

        List<Long> userIds = tips.stream()
            .map(Tip::getUserId)
            .toList();

        List<User> users = userRepository.findAllInIds(userIds);

        List<GetTipResposne> tipResponse = tips.stream()
                .flatMap(tip -> users.stream()
                    .filter(user -> Objects.equals(tip.getUserId(), user.getId()))
                    .map(user -> BoardGameMapper.toGetTipResponse(user, tip)))
                .toList();

        return BoardGameMapper.toGetBoardGameResponse(boardGame, tipResponse);
    }
}
