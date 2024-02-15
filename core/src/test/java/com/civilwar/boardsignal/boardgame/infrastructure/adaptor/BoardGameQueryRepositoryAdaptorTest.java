package com.civilwar.boardsignal.boardgame.infrastructure.adaptor;

import static com.civilwar.boardsignal.boardgame.domain.constant.Category.*;
import static com.civilwar.boardsignal.boardgame.domain.constant.Category.FAMILY;
import static com.civilwar.boardsignal.boardgame.domain.constant.Category.WAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.COMPLETABLE_FUTURE;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DisplayName("[BoardGameQueryRepositoryAdaptor 테스트]")
class BoardGameQueryRepositoryAdaptorTest extends DataJpaTestSupport {

    @Autowired
    private BoardGameQueryRepositoryAdaptor boardGameQueryAdaptor;

    @Autowired
    private BoardGameRepositoryAdaptor boardGameAdaptor;

    private final int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 5;

    @BeforeEach
    void setUp() {
        BoardGameCategory warGame = BoardGameFixture.getBoardGameCategory(WAR);
        BoardGameCategory familyGame = BoardGameFixture.getBoardGameCategory(FAMILY);

        BoardGame boardGame = BoardGameFixture.getBoardGame(List.of(warGame, familyGame));

        boardGameAdaptor.save(boardGame);
    }

    @Test
    @DisplayName("[난이도가 보통인 게임을 모두 조회할 수 있다.]")
    void findAllWithDifficulty() {
        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            "보통",
            null,
            null
        );

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Page<BoardGame> all = boardGameQueryAdaptor.findAll(condition, pageRequest);
        List<BoardGame> boardgames = all.getContent();

        assertAll(
            () -> assertThat(all.getTotalElements()).isEqualTo(1),
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
        );

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Page<BoardGame> all = boardGameQueryAdaptor.findAll(condition, pageRequest);
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
        ); // playTime 조건은 충족하지만 난이도 조건이 충족되지 않은 상황

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Page<BoardGame> all = boardGameQueryAdaptor.findAll(condition, pageRequest);
        BoardGame findBoardGame = all.getContent().get(0);

        List<Category> categories = findBoardGame.getCategories().stream()
            .map(BoardGameCategory::getCategory)
            .toList();

        assertThat(categories).contains(WAR);
    }

    @Test
    @DisplayName("[조건을 하나라도 만족 못할 시 조회하지 못한다.]")
    void findAllWithNothingResult() {
        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            "어려움",
            null,
            30
        ); // playTime 조건은 충족하지만 난이도 조건이 충족되지 않은 상황

        PageRequest pageRequest = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
        Page<BoardGame> all = boardGameQueryAdaptor.findAll(condition, pageRequest);

        assertThat(all.getTotalElements()).isZero();
    }
}