package com.civilwar.boardsignal.boardgame.application;

import static com.civilwar.boardsignal.boardgame.domain.constant.Category.FAMILY;
import static com.civilwar.boardsignal.boardgame.domain.constant.Category.WAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGameCategory;
import com.civilwar.boardsignal.boardgame.domain.repository.BoardGameQueryRepository;
import com.civilwar.boardsignal.boardgame.dto.request.BoardGameSearchCondition;
import com.civilwar.boardsignal.boardgame.dto.response.BoardGamePageResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetAllBoardGamesResponse;
import com.civilwar.boardsignal.fixture.BoardGameFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@DisplayName("[BoardGameService 테스트]")
@ExtendWith(MockitoExtension.class)
class BoardGameServiceTest {

    private final int PAGE_NUMBER = 0;
    private final int PAGE_SIZE = 5;
    @Mock
    private BoardGameQueryRepository boardGameQueryRepository;
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
        BoardGameCategory warGame = BoardGameFixture.getBoardGameCategory(WAR);
        BoardGameCategory familyGame = BoardGameFixture.getBoardGameCategory(FAMILY);
        BoardGame boardGame = BoardGameFixture.getBoardGame(List.of(warGame, familyGame));

        BoardGameSearchCondition condition = new BoardGameSearchCondition(
            "어려움",
            List.of(),
            null
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

}