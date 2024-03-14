package com.civilwar.boardsignal.auth.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.civilwar.boardsignal.auth.domain.model.Token;
import com.civilwar.boardsignal.auth.exception.AuthErrorCode;
import com.civilwar.boardsignal.auth.infrastructure.JwtTokenProvider;
import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.common.support.ApiTestSupport;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.constants.Role;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("[AuthApiController 테스트]")
class AuthApiControllerTest extends ApiTestSupport {

    private final String REFRESHTOKEN_NAME = "RefreshToken_Id";
    private final String BEARER = "Bearer ";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private Supplier<LocalDateTime> nowTime;

    @Test
    @DisplayName("[Cookie에 담긴 RefreshToken ID를 통해 AccessToken을 재발급 받는다]")
    void issueAccessTokenTest() throws Exception {
        //given
        User userFixture = UserFixture.getUserFixture("232345", "testURL");
        userRepository.save(userFixture);

        Token token = jwtTokenProvider.createToken(userFixture.getId(), Role.USER);
        Cookie cookie = new Cookie(REFRESHTOKEN_NAME, token.refreshTokenId());

        //then
        mockMvc.perform(
                get("/api/v1/auth/reissue"
                ).cookie(cookie)
                    .header(AUTHORIZATION, BEARER + token.accessToken())
            )
            .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    @DisplayName("[회원은 로그아웃 할 수 있다]")
    void logoutTest1() throws Exception {
        //given
        User userFixture = UserFixture.getUserFixture("232345", "testURL");
        userRepository.save(userFixture);

        Token token = jwtTokenProvider.createToken(userFixture.getId(), Role.USER);
        Cookie cookie = new Cookie(REFRESHTOKEN_NAME, token.refreshTokenId());

        //then
        mockMvc.perform(
                post("/api/v1/auth/logout"
                ).cookie(cookie)
                    .header(AUTHORIZATION, BEARER + token.accessToken())
            )
            .andExpect(jsonPath("$.logoutResult").value(true));
    }

    @Test
    @DisplayName("[잘못된 refreshTokenId를 갖고있다면 로그아웃에 실패한다]")
    void logoutTest2() throws Exception {
        //given
        User userFixture = UserFixture.getUserFixture("232345", "testURL");
        userRepository.save(userFixture);

        Token token = jwtTokenProvider.createToken(userFixture.getId(), Role.USER);
        Cookie cookie = new Cookie(REFRESHTOKEN_NAME, "fakeId");

        //then
        mockMvc.perform(
                post("/api/v1/auth/logout"
                ).cookie(cookie)
                    .header(AUTHORIZATION, BEARER + token.accessToken())
            )
            .andExpect(jsonPath("$.logoutResult").value(false));
    }

    @Test
    @Transactional
    @DisplayName("[AccessToken의 사용자 정보를 반환한다.]")
    void getLoginUserInfoTest() throws Exception {
        //given
        User loginUserEntity = userRepository.findById(loginUser.getId())
            .orElseThrow();

        String updateNickname = "업데이트된 닉네임";
        List<Category> categories = List.of(Category.CUSTOMIZABLE, Category.PARTY);
        String subwayLine = "2호선";
        String subwayStation = "사당역";
        String image = "update image";
        loginUserEntity.updateUser(updateNickname, categories, subwayLine, subwayStation, image);

        LocalDateTime now = LocalDateTime.of(2024, 3, 4, 9, 27, 0);
        given(nowTime.get()).willReturn(now);

        int expectedAge = nowTime.get().getYear() - loginUser.getBirth() + 1;

        //then
        mockMvc.perform(get("/api/v1/auth")
                .header(AUTHORIZATION, accessToken))
            .andExpect(jsonPath("$.id").value(loginUser.getId()))
            .andExpect(jsonPath("$.email").value(loginUser.getEmail()))
            .andExpect(jsonPath("$.nickname").value(updateNickname))
            .andExpect(jsonPath("$.ageGroup").value(loginUser.getAgeGroup().getDescription()))
            .andExpect(jsonPath("$.gender").value(loginUser.getGender().getDescription()))
            .andExpect(jsonPath("$.isJoined").value(true))
            .andExpect(jsonPath("$.age").value(expectedAge))
            .andExpect(jsonPath("$.subwayLine").value(subwayLine))
            .andExpect(jsonPath("$.subwayStation").value(subwayStation))
            .andExpect(jsonPath("$.categories.size()").value(2));
    }

    @Test
    @DisplayName("[인증이 안 된 사용자가 접근 시 401 에러와 전용 에러 메시지 & 코드를 반환한다.]")
    void exceptionHandlingTest() throws Exception {

        mockMvc.perform(
                get("/api/v1/users/" + loginUser.getId()))
            .andExpect(jsonPath("$.message").value(AuthErrorCode.AUTH_REQUIRED.getMessage()))
            .andExpect(jsonPath("$.code").value(AuthErrorCode.AUTH_REQUIRED.getCode()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("[잘못된 AccessToken이 넘어온다면 401 에러와 전용 에러 메시지 & 코드를 반환한다.]")
    void exceptionHandlingTest2() throws Exception {

        String wrongToken = "Bearer WrongToken";

        mockMvc.perform(
                get("/api/v1/users/" + loginUser.getId())
                    .header(AUTHORIZATION, wrongToken))
            .andExpect(jsonPath("$.message").value(AuthErrorCode.AUTH_TOKEN_MALFORMED.getMessage()))
            .andExpect(jsonPath("$.code").value(AuthErrorCode.AUTH_TOKEN_MALFORMED.getCode()))
            .andExpect(status().isUnauthorized());
    }
}