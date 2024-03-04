package com.civilwar.boardsignal.boardgame.application;

import static com.civilwar.boardsignal.boardgame.domain.constant.Category.FAMILY;
import static com.civilwar.boardsignal.boardgame.domain.constant.Category.PARTY;
import static com.civilwar.boardsignal.boardgame.domain.constant.Category.WAR;
import static com.civilwar.boardsignal.boardgame.exception.BoardGameErrorCode.AlREADY_TIP_ADDED;
import static com.civilwar.boardsignal.boardgame.exception.BoardGameErrorCode.NOT_FOUND_BOARD_GAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGameCategory;
import com.civilwar.boardsignal.boardgame.domain.entity.Like;
import com.civilwar.boardsignal.boardgame.domain.entity.Tip;
import com.civilwar.boardsignal.boardgame.domain.entity.Wish;
import com.civilwar.boardsignal.boardgame.domain.repository.BoardGameQueryRepository;
import com.civilwar.boardsignal.boardgame.domain.repository.LikeRepository;
import com.civilwar.boardsignal.boardgame.domain.repository.TipRepository;
import com.civilwar.boardsignal.boardgame.domain.repository.WishRepository;
import com.civilwar.boardsignal.boardgame.dto.request.AddTipRequest;
import com.civilwar.boardsignal.boardgame.dto.request.BoardGameSearchCondition;
import com.civilwar.boardsignal.boardgame.dto.response.AddTipResposne;
import com.civilwar.boardsignal.boardgame.dto.response.BoardGamePageResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetAllBoardGamesResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetBoardGameResponse;
import com.civilwar.boardsignal.boardgame.dto.response.LikeTipResponse;
import com.civilwar.boardsignal.boardgame.dto.response.WishBoardGameResponse;
import com.civilwar.boardsignal.boardgame.exception.BoardGameErrorCode;
import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.common.exception.ValidationException;
import com.civilwar.boardsignal.fixture.BoardGameFixture;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("[BoardGameService 테스트]")
@ExtendWith(MockitoExtension.class)
class BoardGameServiceTest {

    private final int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 5;
    @Mock
    private BoardGameQueryRepository boardGameQueryRepository;

    @Mock
    private TipRepository tipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WishRepository wishRepository;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private BoardGameService boardGameService;

    @Test
    @DisplayName("[조건에 맞는 보드게임들을 조회할 수 있다.]")
    void findAll() {
        BoardGameCategory warGame = BoardGameFixture.getBoardGameCategory(WAR);
        BoardGameCategory familyGame = BoardGameFixture.getBoardGameCategory(FAMILY);
        BoardGame boardGame = BoardGameFixture.getBoardGame(List.of(warGame, familyGame));

        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            "보통",
            List.of(),
            null
            , null
        );

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        given(boardGameQueryRepository.findAll(condition, pageRequest))
            .willReturn(new PageImpl<>(List.of(boardGame)));

        GetAllBoardGamesResponse findBoardGame = boardGameService.getAllBoardGames(
            condition,
            pageRequest
        ).boardGamesInfos().get(0);

        assertAll(
            () -> assertThat(findBoardGame.name()).isEqualTo(boardGame.getTitle()),
            () -> assertThat(findBoardGame.categories()).hasSameSizeAs(boardGame.getCategories()),
            () -> assertThat(findBoardGame.difficulty()).isEqualTo(
                boardGame.getDifficulty().getDescription()),
            () -> assertThat(findBoardGame.minParticipants()).isEqualTo(
                boardGame.getMinParticipants()),
            () -> assertThat(findBoardGame.maxParticipants()).isEqualTo(
                boardGame.getMaxParticipants()),
            () -> assertThat(findBoardGame.fromPlayTime()).isEqualTo(boardGame.getFromPlayTime()),
            () -> assertThat(findBoardGame.toPlayTime()).isEqualTo(boardGame.getToPlayTime()),
            () -> assertThat(findBoardGame.wishCount()).isEqualTo(boardGame.getWishCount()),
            () -> assertThat(findBoardGame.imageUrl()).isEqualTo(boardGame.getMainImageUrl())
        );
    }

    @Test
    @DisplayName("[조건을 만족하지 못하는 보드게임은 조회되지 않는다]")
    void findAllWithWrongCondition() {
        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            "어려움",
            List.of(),
            null
            , null
        );

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        given(boardGameQueryRepository.findAll(condition, pageRequest))
            .willReturn(new PageImpl<>(List.of()));

        BoardGamePageResponse<GetAllBoardGamesResponse> boardGames = boardGameService.getAllBoardGames(
            condition,
            pageRequest
        );

        assertThat(boardGames.size()).isZero();
    }

    @Test
    @DisplayName("[사용자는 보드게임을 찜할 수 있다.")
    void wishBoardGame() {
        User user = UserFixture.getUserFixture("provider", "https~");
        ReflectionTestUtils.setField(user, "id", 1L);

        BoardGame boardGame = BoardGameFixture.getBoardGame(
            List.of(BoardGameFixture.getBoardGameCategory(WAR))
        );
        ReflectionTestUtils.setField(boardGame, "id", 1L);

        Wish wish = BoardGameFixture.getWish(user.getId(), boardGame.getId());

        given(boardGameQueryRepository.findByIdWithLock(1L)).willReturn(Optional.of(boardGame));
        given(
            wishRepository.findByUserIdAndBoardGameId(user.getId(), boardGame.getId())).willReturn(
            Optional.empty());
        given(wishRepository.save(any(Wish.class))).willReturn(wish);

        boardGameService.wishBoardGame(user, boardGame.getId());

        assertThat(boardGame.getWishCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("[이미 찜했던 게임에 대해 찜 등록 요청을 하면 찜이 취소된다]")
    void cancelWish() {
        User user = UserFixture.getUserFixture("provider", "https~");
        ReflectionTestUtils.setField(user, "id", 1L);

        BoardGame boardGame = BoardGameFixture.getBoardGame(
            List.of(BoardGameFixture.getBoardGameCategory(WAR))
        );
        int prevWishCount = boardGame.getWishCount();
        ReflectionTestUtils.setField(boardGame, "id", 1L);

        Wish wish = BoardGameFixture.getWish(user.getId(), boardGame.getId());

        given(boardGameQueryRepository.findByIdWithLock(1L)).willReturn(Optional.of(boardGame));
        given(
            wishRepository.findByUserIdAndBoardGameId(user.getId(), boardGame.getId())).willReturn(
            Optional.of(wish));

        WishBoardGameResponse response = boardGameService.wishBoardGame(user, boardGame.getId());

        assertThat(response.wishCount()).isEqualTo(prevWishCount - 1);
    }

    @Test
    @DisplayName("[보드게임을 찜할 때 존재하지 않는 보드게임이면 예외가 발생한다]")
    void wishBoardGameWithException() {
        User user = UserFixture.getUserFixture("provider", "https~");
        ReflectionTestUtils.setField(user, "id", 1L);

        BoardGame boardGame = BoardGameFixture.getBoardGame(
            List.of(BoardGameFixture.getBoardGameCategory(WAR))
        );
        ReflectionTestUtils.setField(boardGame, "id", 1L);

        given(boardGameQueryRepository.findByIdWithLock(1L)).willReturn(Optional.empty());

        ThrowingCallable when = () -> boardGameService.wishBoardGame(user, boardGame.getId());
        assertThatThrownBy(when)
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(NOT_FOUND_BOARD_GAME.getMessage());
    }

    @Test
    @DisplayName("[사용자는 보드게임에 공략을 추가할 수 있다]")
    void addTip() {
        User user = UserFixture.getUserFixture("provider", "https~");
        ReflectionTestUtils.setField(user, "id", 1L);

        BoardGame boardGame = BoardGameFixture.getBoardGame(
            List.of(BoardGameFixture.getBoardGameCategory(WAR))
        );
        ReflectionTestUtils.setField(boardGame, "id", 1L);

        AddTipRequest request = new AddTipRequest("개꿀팁 공유합니다~");
        Tip tip = BoardGameFixture.getTip(user.getId(), boardGame.getId(), request.content());

        given(tipRepository.findByBoardGameIdAndUserId(boardGame.getId(), user.getId())).willReturn(
            Optional.empty());
        given(tipRepository.save(any(Tip.class))).willReturn(tip);

        AddTipResposne response = boardGameService.addTip(user, boardGame.getId(), request);

        assertThat(response.content()).isEqualTo(request.content());
    }

    @Test
    @DisplayName("[이미 등록한 공략이 있는 보드게임이라면 예외가 발생한다.]")
    void addTipAlreadyAdded() {
        User user = UserFixture.getUserFixture("provider", "https~");
        ReflectionTestUtils.setField(user, "id", 1L);

        BoardGame boardGame = BoardGameFixture.getBoardGame(
            List.of(BoardGameFixture.getBoardGameCategory(WAR))
        );
        ReflectionTestUtils.setField(boardGame, "id", 1L);

        AddTipRequest request = new AddTipRequest("개꿀팁 공유합니다~");
        Tip tip = BoardGameFixture.getTip(user.getId(), boardGame.getId(), request.content());

        given(tipRepository.findByBoardGameIdAndUserId(boardGame.getId(), user.getId())).willReturn(
            Optional.of(tip));

        ThrowingCallable when = () -> boardGameService.addTip(user, boardGame.getId(), request);
        assertThatThrownBy(when)
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining(AlREADY_TIP_ADDED.getMessage());
    }

    @Test
    @DisplayName("[보드게임 상세정보를 조회할 수 있다.]")
    void getBoardGame() {
        /**
         * 두명의 회원이 한 보드게임에 공략을 남긴 상황
         * 보드게임 상세조회 시 공략 두개까지 조회되어야함
         */
        //given
        User user1 = UserFixture.getUserFixture("provider", "hhtps~");
        User user2 = UserFixture.getUserFixture2("provider2", "https2~");
        ReflectionTestUtils.setField(user1, "id", 1L);
        ReflectionTestUtils.setField(user2, "id", 2L);

        BoardGameCategory warGame = BoardGameFixture.getBoardGameCategory(WAR);
        BoardGameCategory partyGame = BoardGameFixture.getBoardGameCategory(PARTY);

        BoardGame boardGame = BoardGameFixture.getBoardGame(List.of(warGame, partyGame));
        ReflectionTestUtils.setField(boardGame, "id", 1L);

        Tip tip1 = Tip.of(boardGame.getId(), 1L, "꿀팁1");
        Tip tip2 = Tip.of(boardGame.getId(), 2L, "꿀팁2");
        ReflectionTestUtils.setField(
            tip1,
            "createdAt",
            LocalDateTime.of(2024, 1, 1, 15, 30)
        );
        ReflectionTestUtils.setField(
            tip2,
            "createdAt",
            LocalDateTime.of(2024, 1, 1, 16, 30)
        );

        given(boardGameQueryRepository.findById(boardGame.getId()))
            .willReturn(Optional.of(boardGame));
        given(tipRepository.findAllByBoardGameId(boardGame.getId()))
            .willReturn(List.of(tip1, tip2));
        given(userRepository.findAllInIds(List.of(1L, 2L)))
            .willReturn(List.of(user1, user2));

        //when
        GetBoardGameResponse resposne = boardGameService.getBoardGame(null, boardGame.getId());
        String firstTip = resposne.tips().get(0).content();
        String secondTip = resposne.tips().get(1).content();

        //then
        assertAll(
            () -> assertThat(resposne.name()).isEqualTo(boardGame.getTitle()),
            () -> assertThat(resposne.categories()).hasSize(2),
            () -> assertThat(resposne.categories()).contains(
                warGame.getCategory().getDescription()),
            () -> assertThat(resposne.categories()).contains(
                partyGame.getCategory().getDescription()),
            () -> assertThat(resposne.tips()).hasSize(2),
            () -> assertThat(firstTip).isEqualTo(tip1.getContent()),
            () -> assertThat(secondTip).isEqualTo(tip2.getContent()),
            () -> assertThat(resposne.tips().get(0).nickname()).isEqualTo(user1.getNickname()),
            () -> assertThat(resposne.tips().get(1).nickname()).isEqualTo(user2.getNickname()),
            //로그인 안한 사용자이므로 찜, 좋아요 둘 다 false
            () -> assertThat(resposne.isWished()).isFalse(),
            () -> assertThat(resposne.tips().get(0).isLiked()).isFalse(),
            () -> assertThat(resposne.tips().get(1).isLiked()).isFalse()
        );
    }

    @Test
    @DisplayName("[찜한 보드게임의 공략도 좋아요 한 사용자는 보드게임 조회 시 둘의 여부를 응답받을 수 있다.]")
    void getBoardGameWishAndLikeTip(){
        //given
        User user1 = UserFixture.getUserFixture("provider", "hhtps~");
        User user2 = UserFixture.getUserFixture2("provider2", "https2~");
        User user3 = UserFixture.getUserFixture2("asdasd", "image");
        ReflectionTestUtils.setField(user1, "id", 1L);
        ReflectionTestUtils.setField(user2, "id", 2L);
        ReflectionTestUtils.setField(user3, "id", 3L);

        BoardGameCategory warGame = BoardGameFixture.getBoardGameCategory(WAR);
        BoardGameCategory partyGame = BoardGameFixture.getBoardGameCategory(PARTY);

        BoardGame boardGame = BoardGameFixture.getBoardGame(List.of(warGame, partyGame));
        ReflectionTestUtils.setField(boardGame, "id", 1L);

        Tip tip1 = Tip.of(boardGame.getId(), 1L, "꿀팁1");
        Tip tip2 = Tip.of(boardGame.getId(), 2L, "꿀팁2");
        ReflectionTestUtils.setField(
            tip1,
            "createdAt",
            LocalDateTime.of(2024, 1, 1, 15, 30)
        );
        ReflectionTestUtils.setField(
            tip2,
            "createdAt",
            LocalDateTime.of(2024, 1, 1, 16, 30)
        );
        ReflectionTestUtils.setField(tip1, "id", 1L);
        ReflectionTestUtils.setField(tip2, "id", 2L);

        Like like1 = Like.of(tip1.getId(), user3.getId());
        Like like2 = Like.of(tip2.getId(), user3.getId());
        Wish wish = BoardGameFixture.getWish(user3.getId(), boardGame.getId());

        given(boardGameQueryRepository.findById(boardGame.getId()))
            .willReturn(Optional.of(boardGame));
        given(tipRepository.findAllByBoardGameId(boardGame.getId()))
            .willReturn(List.of(tip1, tip2));
        given(userRepository.findAllInIds(List.of(1L ,2L)))
            .willReturn(List.of(user1, user2));
        given(likeRepository.findAllByUserId(user3.getId()))
            .willReturn(List.of(like1, like2));
        given(wishRepository.findByUserIdAndBoardGameId(user3.getId(), boardGame.getId()))
            .willReturn(Optional.of(wish));

        //when
        GetBoardGameResponse resposne = boardGameService.getBoardGame(user3, boardGame.getId());
        String firstTip = resposne.tips().get(0).content();
        String secondTip = resposne.tips().get(1).content();

        //then
        assertAll(
            () -> assertThat(resposne.name()).isEqualTo(boardGame.getTitle()),
            () -> assertThat(resposne.categories()).hasSize(2),
            () -> assertThat(resposne.categories()).contains(
                warGame.getCategory().getDescription()),
            () -> assertThat(resposne.categories()).contains(
                partyGame.getCategory().getDescription()),
            () -> assertThat(resposne.tips()).hasSize(2),
            () -> assertThat(firstTip).isEqualTo(tip1.getContent()),
            () -> assertThat(secondTip).isEqualTo(tip2.getContent()),
            () -> assertThat(resposne.tips().get(0).nickname()).isEqualTo(user1.getNickname()),
            () -> assertThat(resposne.tips().get(1).nickname()).isEqualTo(user2.getNickname()),
            //보드게임 찜, 공략에 모두 좋아요 한 상태
            () -> assertThat(resposne.isWished()).isTrue(),
            () -> assertThat(resposne.tips().get(0).isLiked()).isTrue(),
            () -> assertThat(resposne.tips().get(1).isLiked()).isTrue()
        );
    }

    @Test
    @DisplayName("[존재하지 않는 보드게임을 조회하려하면 예외가 발생한다]")
    void getBoardGameNotExist() {
        //given
        given(boardGameQueryRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        //when
        ThrowingCallable when = () -> boardGameService.getBoardGame(null, 1L);

        //then
        assertThatThrownBy(when)
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(NOT_FOUND_BOARD_GAME.getMessage());
    }

    @Test
    @DisplayName("[사용자는 검색 키워드를 통해 보드게임을 검색할 수 있다.]")
    void searchWithKeyword() {
        BoardGameCategory warGame = BoardGameFixture.getBoardGameCategory(WAR);
        BoardGameCategory familyGame = BoardGameFixture.getBoardGameCategory(FAMILY);
        BoardGame boardGame = BoardGameFixture.getBoardGame(List.of(warGame, familyGame));

        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            null,
            null,
            null
            , String.valueOf(boardGame.getTitle().charAt(0))
        );

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

        given(boardGameQueryRepository.findAll(condition, pageRequest))
            .willReturn(new PageImpl<>(List.of(boardGame)));

        BoardGamePageResponse<GetAllBoardGamesResponse> boardGames = boardGameService.getAllBoardGames(
            condition,
            pageRequest
        );
        GetAllBoardGamesResponse findBoardGame = boardGames.boardGamesInfos().get(0);

        assertAll(
            () -> assertThat(findBoardGame.name())
                .isEqualTo(boardGame.getTitle()),
            () -> assertThat(findBoardGame.categories())
                .hasSameSizeAs(boardGame.getCategories()),
            () -> assertThat(findBoardGame.difficulty())
                .isEqualTo(boardGame.getDifficulty().getDescription()),
            () -> assertThat(findBoardGame.minParticipants())
                .isEqualTo(boardGame.getMinParticipants()),
            () -> assertThat(findBoardGame.maxParticipants())
                .isEqualTo(boardGame.getMaxParticipants()),
            () -> assertThat(findBoardGame.fromPlayTime())
                .isEqualTo(boardGame.getFromPlayTime()),
            () -> assertThat(findBoardGame.toPlayTime())
                .isEqualTo(boardGame.getToPlayTime()),
            () -> assertThat(findBoardGame.wishCount())
                .isEqualTo(boardGame.getWishCount()),
            () -> assertThat(findBoardGame.imageUrl())
                .isEqualTo(boardGame.getMainImageUrl())
        );
    }

    @Test
    @DisplayName("[보드게임 공략에 대해 좋아요를 할 수 있다.]")
    void likeTip() {
        //given
        User user = UserFixture.getUserFixture("prprp", "gttps");
        ReflectionTestUtils.setField(user, "id", 1L);

        Tip tip = BoardGameFixture.getTip(user.getId(), 1L, "개꿀팁");
        ReflectionTestUtils.setField(tip, "id", 1L);
        int prevLikeCount = tip.getLikeCount();

        given(tipRepository.findByIdWithLock(tip.getId()))
            .willReturn(Optional.of(tip));

        //when
        LikeTipResponse response = boardGameService.likeTip(user, 1L);
        int currLikeCount = response.likeCount();

        //then
        assertThat(currLikeCount).isEqualTo(++prevLikeCount);
    }

    @Test
    @DisplayName("[존재하지 않는 공략에 대해 좋아요를 하면 예외가 발생한다.]")
    void likeTipNotExist() {
        //given
        User user = UserFixture.getUserFixture("prpr", "https~");
        given(tipRepository.findByIdWithLock(any(Long.class)))
            .willReturn(Optional.empty());

        //when
        ThrowingCallable when = () -> boardGameService.likeTip(user, 1L);

        //when
        assertThatThrownBy(when)
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(BoardGameErrorCode.NOT_FOUND_TIP.getMessage());
    }

    @Test
    @DisplayName("[공략의 좋아요를 취소할 수 있다.]")
    void cancelLikeTip() {
        //given
        User user = UserFixture.getUserFixture("prprp", "gttps");
        ReflectionTestUtils.setField(user, "id", 1L);

        Tip tip = BoardGameFixture.getTip(user.getId(), 1L, "개꿀팁");
        ReflectionTestUtils.setField(tip, "id", 1L);
        int prevLikeCount = tip.getLikeCount();

        given(tipRepository.findByIdWithLock(tip.getId()))
            .willReturn(Optional.of(tip));

        //when
        LikeTipResponse response = boardGameService.cancelLikeTip(user, 1L);
        int currLikeCount = response.likeCount();

        //then
        assertThat(currLikeCount).isEqualTo(--prevLikeCount);
    }


}