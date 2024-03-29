package com.civilwar.boardsignal.boardgame.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGameCategory;
import com.civilwar.boardsignal.boardgame.domain.entity.Tip;
import com.civilwar.boardsignal.boardgame.domain.entity.Wish;
import com.civilwar.boardsignal.boardgame.domain.repository.BoardGameRepository;
import com.civilwar.boardsignal.boardgame.domain.repository.TipRepository;
import com.civilwar.boardsignal.boardgame.domain.repository.WishRepository;
import com.civilwar.boardsignal.boardgame.dto.request.ApiAddTipRequest;
import com.civilwar.boardsignal.common.support.ApiTestSupport;
import com.civilwar.boardsignal.fixture.BoardGameFixture;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@DisplayName("[BoardGameController 테스트]")
class BoardGameControllerTest extends ApiTestSupport {

    @Autowired
    private BoardGameRepository boardGameRepository;

    @Autowired
    private TipRepository tipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WishRepository wishRepository;

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
    @DisplayName("[특정 보드게임을 찜 등록 할 수 있다.]")
    void wishBoardGame() throws Exception {
        int prevWishCount = boardGame1.getWishCount();
        //찜 등록
        mockMvc.perform(post("/api/v1/board-games/wish/{boardGameId}",
                boardGame1.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.wishCount").value(prevWishCount + 1));
    }

    @Test
    @DisplayName("[특정 보드게임에 대해 찜 취소를 할 수 있다.]")
    void cancelWish() throws Exception {
        Wish wish = Wish.of(loginUser.getId(), boardGame1.getId());
        wishRepository.save(wish);

        int prevWishCount = boardGame1.getWishCount();

        //찜 취소
        mockMvc.perform(delete("/api/v1/board-games/wish/{boardGameId}",
                boardGame1.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.wishCount").value(--prevWishCount));
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
    @DisplayName("[사용자는 검색 키워드를 통해 원하는 보드게임 목록을 조회할 수 있다.]")
    void searchByKeyword() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("size", "1");
        params.add("searchKeyword", boardGame1.getTitle().substring(0, 1));
        mockMvc.perform(get("/api/v1/board-games")
                .params(params))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.boardGamesInfos[0].name")
                    .value(boardGame1.getTitle()),
                jsonPath("$.boardGamesInfos[0].categories[0]")
                    .value(boardGame2.getCategories().get(0).getCategory().getDescription()),
                jsonPath("$.boardGamesInfos[0].difficulty")
                    .value(boardGame1.getDifficulty().getDescription()),
                jsonPath("$.boardGamesInfos[0].minParticipants")
                    .value(boardGame1.getMinParticipants()),
                jsonPath("$.boardGamesInfos[0].maxParticipants")
                    .value(boardGame1.getMaxParticipants()),
                jsonPath("$.boardGamesInfos[0].fromPlayTime")
                    .value(boardGame1.getFromPlayTime()),
                jsonPath("$.boardGamesInfos[0].toPlayTime")
                    .value(boardGame1.getToPlayTime()),
                jsonPath("$.boardGamesInfos[0].wishCount")
                    .value(boardGame1.getWishCount()),
                jsonPath("$.boardGamesInfos[0].imageUrl")
                    .value(boardGame1.getMainImageUrl()),
                jsonPath("$.size").value(1),
                jsonPath("$.hasNext").value(false)
            );
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
            .andExpectAll(
                status().isOk(),
                jsonPath("$.boardGameId")
                    .value(boardGame1.getId()),
                jsonPath("$.name")
                    .value(boardGame1.getTitle()),
                jsonPath("$.description")
                    .value(boardGame1.getDescription()),
                jsonPath("$.categories[0]")
                    .value(boardGame1.getCategories().get(0).getCategory().getDescription()),
                jsonPath("$.difficulty")
                    .value(boardGame1.getDifficulty().getDescription()),
                jsonPath("$.minParticipants")
                    .value(boardGame1.getMinParticipants()),
                jsonPath("$.maxParticipants")
                    .value(boardGame1.getMaxParticipants()),
                jsonPath("$.fromPlayTime")
                    .value(boardGame1.getFromPlayTime()),
                jsonPath("$.toPlayTime")
                    .value(boardGame1.getToPlayTime()),
                jsonPath("$.wishCount")
                    .value(boardGame1.getWishCount()),
                jsonPath("$.imageUrl")
                    .value(boardGame1.getMainImageUrl()),
                jsonPath("$.tips[0].nickname")
                    .value(user.getNickname()),
                jsonPath("$.tips[0].profileImageUrl")
                    .value(user.getProfileImageUrl()),
                jsonPath("$.tips[0].content")
                    .value(tip.getContent())
            );
    }

    @Test
    @DisplayName("[사용자는 보드게임 공략에 대해 좋아요를 할 수 있다.]")
    void likeTip() throws Exception {
        Tip tip = BoardGameFixture.getTip(1L, 1L, "이게 팁입니다.");
        Tip savedTip = tipRepository.save(tip);
        int prevLikeCount = savedTip.getLikeCount();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/board-games/like/{tipId}", savedTip.getId())
                    .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tipId").value(savedTip.getId()))
            .andExpect(jsonPath("$.likeCount").value(++prevLikeCount));
    }

    @Test
    @DisplayName("[사용자는 보드게임 공략에 대해 좋아요를 취소할 수 있다.]")
    void cancelLikeTip() throws Exception {
        Tip tip = BoardGameFixture.getTip(1L, 1L, "이게 팁입니다.");
        tip.increaseLikeCount();
        Tip savedTip = tipRepository.save(tip);
        int prevLikeCount = savedTip.getLikeCount();

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/board-games/like/{tipId}", savedTip.getId())
                    .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tipId").value(savedTip.getId()))
            .andExpect(jsonPath("$.likeCount").value(--prevLikeCount));
    }

    @Test
    @DisplayName("[자신이 등록한 보드게임 공략을 삭제할 수 있다.]")
    void deleteTip() throws Exception {
        //given
        Tip tip = BoardGameFixture.getTip(1L, 1L, "이게 팁입니다.");
        Tip savedTip = tipRepository.save(tip);
        List<Tip> prevTips = tipRepository.findAllByBoardGameId(savedTip.getBoardGameId());

        //when
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/board-games/tip/{tipId}", savedTip.getId())
                    .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk());

        List<Tip> currTips = tipRepository.findAllByBoardGameId(savedTip.getBoardGameId());

        //then
        assertThat(currTips).hasSize(prevTips.size() - 1);
    }

    @Test
    @DisplayName("[보드게임 상세조회 시 자신의 공략을 조회할 수 있다.]")
    void getBoardGameLoginUser() throws Exception {
        //given
        User savedUser = userRepository.save(loginUser);
        Tip tip = BoardGameFixture.getTip(
            savedUser.getId(),
            boardGame1.getId(),
            "보드게임1의 팁"
        );
        Tip savedTip = tipRepository.save(tip);

        //then
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/board-games/{boardGameId}", boardGame1.getId())
                    .header(AUTHORIZATION, accessToken))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.myTip.tipId").value(savedTip.getId()),
                jsonPath("$.myTip.nickname").value(loginUser.getNickname()),
                jsonPath("$.myTip.profileImageUrl").value(loginUser.getProfileImageUrl()),
                jsonPath("$.myTip.createdAt").value(
                    savedTip.getCreatedAt().truncatedTo(ChronoUnit.MINUTES).toString()),
                jsonPath("$.myTip.content").value(savedTip.getContent()),
                jsonPath("$.myTip.likeCount").value(savedTip.getLikeCount())
            );
    }

    @Test
    @DisplayName("[보드게임 상세 조회 시 등록한 공략이 없다면 나의 공략은 비어있다.]")
    void getBoardGameWithoutMyTip() throws Exception {
        //given
        userRepository.save(loginUser);

        Tip tip = BoardGameFixture.getTip(
            100L,
            boardGame1.getId(),
            "개꿀팁"
        ); // 다른 사람의 공략
        tipRepository.save(tip);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/board-games/{boardGameId}", boardGame1.getId())
                    .header(AUTHORIZATION, accessToken))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.myTip").doesNotExist()
            );
    }

    @Test
    @DisplayName("[로그인 안한 사용자가 조회 시 나의 공략은 비어있다.]")
    void getBoardGameWithoutLogin() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/board-games/{boardGameId}", boardGame1.getId()))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.myTip").doesNotExist()
            );
    }

    @Test
    @DisplayName("[찜한 보드게임 목록을 전체 조회할 수 있다.]")
    void getAllWishBoardGames() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("size", "1");

        Wish wish = Wish.of(loginUser.getId(), boardGame1.getId());
        wishRepository.save(wish);

        mockMvc.perform(get("/api/v1/board-games/wish/{userId}", loginUser.getId())
                .params(params)
                .header(AUTHORIZATION, accessToken))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.boardGamesInfos[0].name")
                    .value(boardGame1.getTitle()),
                jsonPath("$.boardGamesInfos[0].categories[0]")
                    .value(boardGame2.getCategories().get(0).getCategory().getDescription()),
                jsonPath("$.boardGamesInfos[0].difficulty")
                    .value(boardGame1.getDifficulty().getDescription()),
                jsonPath("$.boardGamesInfos[0].minParticipants")
                    .value(boardGame1.getMinParticipants()),
                jsonPath("$.boardGamesInfos[0].maxParticipants")
                    .value(boardGame1.getMaxParticipants()),
                jsonPath("$.boardGamesInfos[0].fromPlayTime")
                    .value(boardGame1.getFromPlayTime()),
                jsonPath("$.boardGamesInfos[0].toPlayTime")
                    .value(boardGame1.getToPlayTime()),
                jsonPath("$.boardGamesInfos[0].wishCount")
                    .value(boardGame1.getWishCount()),
                jsonPath("$.boardGamesInfos[0].imageUrl")
                    .value(boardGame1.getMainImageUrl()),
                jsonPath("$.size").value(1),
                jsonPath("$.hasNext").value(false)
            );
    }
}