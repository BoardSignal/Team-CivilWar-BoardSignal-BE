package com.civilwar.boardsignal.boardgame.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGameCategory;
import com.civilwar.boardsignal.boardgame.domain.repository.BoardGameRepository;
import com.civilwar.boardsignal.common.support.ApiTestSupport;
import com.civilwar.boardsignal.fixture.BoardGameFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@DisplayName("[BoardGameController 테스트]")
class BoardGameControllerTest extends ApiTestSupport {

    @Autowired
    private BoardGameRepository boardGameRepository;

    private BoardGame boardGame1;
    private BoardGame boardGame2;

    @BeforeEach
    void setUp() {
        BoardGameCategory warGame = BoardGameFixture.getBoardGameCategory(Category.WAR);
        BoardGameCategory partyGame = BoardGameFixture.getBoardGameCategory(Category.PARTY);
        BoardGameCategory familyGame = BoardGameFixture.getBoardGameCategory(
            Category.FAMILY
        );

        boardGame1 = BoardGameFixture.getBoardGame(List.of(warGame, partyGame)); // 난이도 -> 보통
        boardGame2 = BoardGameFixture.getBoardGame2(List.of(warGame, familyGame)); // 난이도 -> 어려움

        boardGameRepository.saveAll(List.of(boardGame1, boardGame2));
    }

    @Test
    @DisplayName("[조건에 맞는 보드게임을 전체 조회할 수 있다.]")
    void findAll() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("size", "1");
        params.add("difficulty", "어려움");
        params.put("categories", List.of("워게임", "가족게임"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/board-games")
                .params(params))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.boardGamesInfos[0].name").value(boardGame2.getTitle()))
            .andExpect(
                jsonPath("$.boardGamesInfos[0].categories[0]").value(Category.WAR.getDescription()))
            .andExpect(jsonPath("$.boardGamesInfos[0].difficulty").value(
                boardGame2.getDifficulty().getDescription()))
            .andExpect(jsonPath("$.boardGamesInfos[0].minParticipants").value(
                boardGame2.getMinParticipants()))
            .andExpect(jsonPath("$.boardGamesInfos[0].maxParticipants").value(
                boardGame2.getMaxParticipants()))
            .andExpect(
                jsonPath("$.boardGamesInfos[0].fromPlayTime").value(boardGame2.getFromPlayTime()))
            .andExpect(
                jsonPath("$.boardGamesInfos[0].toPlayTime").value(boardGame2.getToPlayTime()))
            .andExpect(jsonPath("$.boardGamesInfos[0].wishCount").value(boardGame2.getWishCount()))
            .andExpect(
                jsonPath("$.boardGamesInfos[0].imageUrl").value(boardGame2.getMainImageUrl()))
            .andExpect(jsonPath("$.size").value(1))
            .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("[특정 보드게임을 찜 등록을 하거나 취소할 수 있다.]")
    void wishBoardGame() throws Exception {
        int prevWishCount = boardGame1.getWishCount();
        //찜 등록
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/board-games/wish/{boardGameId}",
                    boardGame1.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.wishCount").value(prevWishCount + 1));
        //찜 취소
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/board-games/wish/{boardGameId}",
                    boardGame1.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.wishCount").value(prevWishCount));
    }

}