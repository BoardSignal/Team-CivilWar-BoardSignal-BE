package com.civilwar.boardsignal.boardgame.infrastructure.adaptor;

import static com.civilwar.boardsignal.boardgame.domain.constant.Category.FAMILY;
import static com.civilwar.boardsignal.boardgame.domain.constant.Category.PARTY;
import static com.civilwar.boardsignal.boardgame.domain.constant.Category.WAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGameCategory;
import com.civilwar.boardsignal.boardgame.dto.request.BoardGameSearchCondition;
import com.civilwar.boardsignal.common.support.DataJpaTestSupport;
import com.civilwar.boardsignal.fixture.BoardGameFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

@DisplayName("[BoardGameQueryRepositoryAdaptor 테스트]")
class BoardGameQueryRepositoryAdaptorTest extends DataJpaTestSupport {

    private final int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 5;
    @Autowired
    private BoardGameQueryRepositoryAdaptor boardGameQueryAdaptor;
    @Autowired
    private BoardGameRepositoryAdaptor boardGameAdaptor;

    private BoardGame boardGame;

    private BoardGame boardGame2;

    @BeforeEach
    void setUp() {
        BoardGameCategory warGame = BoardGameFixture.getBoardGameCategory(WAR);
        BoardGameCategory familyGame = BoardGameFixture.getBoardGameCategory(FAMILY);
        BoardGameCategory partyGame = BoardGameFixture.getBoardGameCategory(PARTY);

        boardGame = BoardGameFixture.getBoardGame(List.of(warGame, familyGame));
        boardGame2 = BoardGameFixture.getBoardGame2(List.of(partyGame));

        boardGameAdaptor.save(boardGame);
        boardGameAdaptor.save(boardGame2);
    }

    @Test
    @DisplayName("[난이도가 보통인 게임을 모두 조회할 수 있다.]")
    void findAllWithDifficulty() {
        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            "보통",
            null,
            null
            , ""
        );

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Slice<BoardGame> all = boardGameQueryAdaptor.findAll(condition, pageRequest);
        List<BoardGame> boardgames = all.getContent();

        assertAll(
            () -> assertThat(all.getNumberOfElements()).isEqualTo(1),
            () -> assertThat(boardgames).hasSize(1)
        );
    }

    @Test
    @DisplayName("[플레이 시간이 특정 범위 안에 있는 보드게임을 다 조회할 수 있다.]")
    void findAllWithPlayTime() {
        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            null,
            null,
            30
            , null
        );

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Slice<BoardGame> all = boardGameQueryAdaptor.findAll(condition, pageRequest);
        BoardGame findBoardGame = all.getContent().get(0);

        assertAll( // 조회된 보드게임의 플레이시간 범위안에 조건 플레이시간이 있는 지 검증
            () -> assertThat(findBoardGame.getFromPlayTime()).isLessThanOrEqualTo(
                condition.playTime()),
            () -> assertThat(findBoardGame.getToPlayTime()).isGreaterThanOrEqualTo(
                condition.playTime())
        );
    }

    @Test
    @DisplayName("[특정 카테고리를 포함하는 보드게임을 다 조회할 수 있다.]")
    void findAllWithCategories() {
        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            null,
            List.of("워게임", "가족게임"),
            null
            , null
        ); // playTime 조건은 충족하지만 난이도 조건이 충족되지 않은 상황

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Slice<BoardGame> all = boardGameQueryAdaptor.findAll(condition, pageRequest);
        BoardGame findBoardGame = all.getContent().get(0);

        List<Category> categories = findBoardGame.getCategories().stream()
            .map(BoardGameCategory::getCategory)
            .toList();

        assertThat(categories).contains(WAR);
    }

    @Test
    @DisplayName("[특정 카테고리를 포함하는 보드게임을 다 조회할 수 있다.(2)]")
    void findAllWithCategories2() {
        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            null,
            List.of("워게임", "파티게임"),
            null
            , null
        );

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Slice<BoardGame> all = boardGameQueryAdaptor.findAll(condition, pageRequest);
        BoardGame findBoardGame = all.getContent().get(0);

        List<Category> categories = findBoardGame.getCategories().stream()
            .map(BoardGameCategory::getCategory)
            .toList();

        assertThat(categories).contains(WAR);
        assertThat(all.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("[조건을 하나라도 만족 못할 시 조회하지 못한다.]")
    void findAllWithNothingResult() {
        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            "어려움",
            null,
            30
            , null
        ); // playTime 조건은 충족하지만 난이도 조건이 충족되지 않은 상황

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Slice<BoardGame> all = boardGameQueryAdaptor.findAll(condition, pageRequest);

        assertThat(all.getNumberOfElements()).isZero();
    }

    @Test
    @DisplayName("[무한스크롤 시 다음 보드게임 정보가 있을 시 hasText값은 True가 반환된다.]")
    void slice() {
        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            null,
            List.of("워게임", "파티게임"),
            null
            , null
        );

        //총 2개가 반환되어 페이지 사이즈 요청을 1로 하면 다음 보드게임이 있으므로 true가 되어야함
        PageRequest pageRequest = PageRequest.of(0, 1);
        Slice<BoardGame> all = boardGameQueryAdaptor.findAll(condition, pageRequest);

        assertThat(all.hasNext()).isTrue();
    }

    @Test
    @DisplayName("[검색 키워드를 통해 조건을 충족하는 보드게임 하나를 조회한다.]")
    void searchKeyWord() {
        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            null,
            null,
            null
            , boardGame.getDescription().substring(1, 3)
        );

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Slice<BoardGame> all = boardGameQueryAdaptor.findAll(condition, pageRequest);
        BoardGame findBoardGame = all.getContent().get(0);

        assertAll(
            () -> assertThat(findBoardGame.getTitle())
                .isEqualTo(boardGame.getTitle()),
            () -> assertThat(findBoardGame.getDescription())
                .isEqualTo(boardGame.getDescription()),
            () -> assertThat(findBoardGame.getDifficulty())
                .isEqualTo(boardGame.getDifficulty()),
            () -> assertThat(findBoardGame.getMinParticipants())
                .isEqualTo(boardGame.getMinParticipants()),
            () -> assertThat(findBoardGame.getMaxParticipants())
                .isEqualTo(boardGame.getMaxParticipants()),
            () -> assertThat(findBoardGame.getFromPlayTime())
                .isEqualTo(boardGame.getFromPlayTime()),
            () -> assertThat(findBoardGame.getToPlayTime())
                .isEqualTo(boardGame.getToPlayTime()),
            () -> assertThat(findBoardGame.getCategories().get(0).getCategory())
                .isEqualTo(boardGame.getCategories().get(0).getCategory()),
            () -> assertThat(all.getContent()).doesNotContain(boardGame2)
        );
    }

    @Test
    @DisplayName("[검색 키워드 조건을 만족하는 보드게임 두개를 조회한다.]")
    void searchKeyWordAll() {
        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            null,
            null,
            null
            , "게임"
        );

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Slice<BoardGame> all = boardGameQueryAdaptor.findAll(condition, pageRequest);
        BoardGame firstGame = all.getContent().get(0);
        BoardGame secondGame = all.getContent().get(1);

        assertAll(
            () -> assertThat(firstGame.getTitle()).isEqualTo(boardGame.getTitle()),
            () -> assertThat(secondGame.getTitle()).isEqualTo(boardGame2.getTitle())
        );
    }

    @Test
    @DisplayName("[보드게임 아이디 리스트안에 속하는 보드게임을 전체 조회할 수 있다.]")
    void findAllInIds() {
        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Slice<BoardGame> boardGames = boardGameQueryAdaptor.findAllInIds(
            List.of(boardGame.getId(), boardGame2.getId()),
            pageRequest
        );

        List<BoardGame> gameList = boardGames.getContent();

        assertThat(gameList).contains(boardGame).contains(boardGame2);
    }

    @Test
    @DisplayName("[보드게임이 두개 있을 때 offset별로 보드게임을 조회할 수 있다.")
    void findAllBoardGameWithOffset() {
        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            null,
            null,
            null
            , null
        );
        PageRequest pageRequest1 = PageRequest.of(0, 1);
        PageRequest pageRequest2 = PageRequest.of(1, 1);
        BoardGame findBoardGame1 = boardGameQueryAdaptor.findAll(condition, pageRequest1)
            .getContent().get(0);
        BoardGame findBoardGame2 = boardGameQueryAdaptor.findAll(condition, pageRequest2)
            .getContent().get(0);

        assertThat(findBoardGame1.getTitle()).isEqualTo(boardGame.getTitle());
        assertThat(findBoardGame2.getTitle()).isEqualTo(boardGame2.getTitle());


    }
}