package com.civilwar.boardsignal.boardgame.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGameCategory;
import com.civilwar.boardsignal.boardgame.domain.entity.Tip;
import com.civilwar.boardsignal.boardgame.domain.repository.BoardGameRepository;
import com.civilwar.boardsignal.boardgame.domain.repository.TipRepository;
import com.civilwar.boardsignal.boardgame.dto.request.ApiAddTipRequest;
import com.civilwar.boardsignal.common.support.ApiTestSupport;
import com.civilwar.boardsignal.fixture.BoardGameFixture;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@DisplayName("[BoardGameController 테스트]")
@Slf4j
class BoardGameControllerTest extends ApiTestSupport {

    @Autowired
    private BoardGameRepository boardGameRepository;

    @Autowired
    private TipRepository tipRepository;

    @Autowired
    private UserRepository userRepository;

    private BoardGame boardGame1;

    private BoardGame boardGame2;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFixture.getUserFixture("provider", "https~");
        userRepository.save(user);

        BoardGameCategory warGame = BoardGameFixture.getBoardGameCategory(Category.WAR);
        BoardGameCategory wargame2 = BoardGameFixture.getBoardGameCategory(Category.WAR);
        BoardGameCategory partyGame = BoardGameFixture.getBoardGameCategory(Category.PARTY);
        BoardGameCategory familyGame = BoardGameFixture.getBoardGameCategory(
            Category.FAMILY
        );

        boardGame1 = BoardGameFixture.getBoardGame(List.of(warGame, partyGame)); // 난이도 -> 보통
        boardGame2 = BoardGameFixture.getBoardGame2(List.of(wargame2, familyGame)); // 난이도 -> 어려움

        boardGameRepository.saveAll(List.of(boardGame1, boardGame2));
    }

    @Test
    @DisplayName("[조건에 맞는 보드게임을 전체 조회할 수 있다.]")
    void findAll() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("size", "1");
        params.add("difficulty", "어려움");
        params.put("categories", List.of("워게임", "가족게임"));
        mockMvc.perform(get("/api/v1/board-games")
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
        mockMvc.perform(post("/api/v1/board-games/wish/{boardGameId}",
                boardGame1.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.wishCount").value(prevWishCount + 1));
        //찜 취소
        mockMvc.perform(post("/api/v1/board-games/wish/{boardGameId}",
                boardGame1.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.wishCount").value(prevWishCount));
    }

    @Test
    @DisplayName("[사용자는 보드게임에 공략을 추가할 수 있다.]")
    void addTip() throws Exception {
        ApiAddTipRequest request = new ApiAddTipRequest("꿀팁입니다.");

        mockMvc.perform(post(
                "/api/v1/board-games/tip/{boardGameId}", boardGame1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value(request.content()));
    }

    @Test
    @DisplayName("[사용자는 보드게임 상세정보를 조회할 수 있다]")
    void getBoardGame() throws Exception {
        //given
        Tip tip = Tip.of(boardGame1.getId(), user.getId(), "꿀팁입니다");
        tipRepository.save(tip);

        //then
        mockMvc.perform(
                get("/api/v1/board-games/{boardGameId}", boardGame1.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.boardGameId")
                .value(boardGame1.getId()))
            .andExpect(jsonPath("$.name")
                .value(boardGame1.getTitle()))
            .andExpect(jsonPath("$.description")
                .value(boardGame1.getDescription()))
            .andExpect(jsonPath("$.categories[0]")
                .value(boardGame1.getCategories().get(0).getCategory().getDescription()))
            .andExpect(jsonPath("$.difficulty")
                .value(boardGame1.getDifficulty().getDescription()))
            .andExpect(jsonPath("$.minParticipants")
                .value(boardGame1.getMinParticipants()))
            .andExpect(jsonPath("$.maxParticipants")
                .value(boardGame1.getMaxParticipants()))
            .andExpect(jsonPath("$.fromPlayTime")
                .value(boardGame1.getFromPlayTime()))
            .andExpect(jsonPath("$.toPlayTime")
                .value(boardGame1.getToPlayTime()))
            .andExpect(jsonPath("$.wishCount")
                .value(boardGame1.getWishCount()))
            .andExpect(jsonPath("$.imageUrl")
                .value(boardGame1.getMainImageUrl()))
            .andExpect(jsonPath("$.tips[0].nickname")
                .value(user.getNickname()))
            .andExpect(jsonPath("$.tips[0].profileImageUrl")
                .value(user.getProfileImageUrl()))
            .andExpect(jsonPath("$.tips[0].content")
                .value(tip.getContent()));

    }


}