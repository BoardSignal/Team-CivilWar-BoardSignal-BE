package com.civilwar.boardsignal.auth.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.civilwar.boardsignal.auth.domain.model.Token;
import com.civilwar.boardsignal.auth.infrastructure.JwtTokenProvider;
import com.civilwar.boardsignal.common.support.ApiTestSupport;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.constants.Role;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("[AuthApiController 테스트]")
class AuthApiControllerTest extends ApiTestSupport {

    private final String BEARER = "Bearer ";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("[Cookie에 담긴 RefreshToken ID를 통해 AccessToken을 재발급 받는다]")
    void issueAccessTokenTest() throws Exception {
        //given
        User userFixture = UserFixture.getUserFixture("232345", "testURL");
        userRepository.save(userFixture);

        Token token = jwtTokenProvider.createToken(userFixture.getId(), Role.USER);
        Cookie cookie = new Cookie("RefreshTokenId", token.refreshTokenId());

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
        Cookie cookie = new Cookie("RefreshTokenId", token.refreshTokenId());

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
        Cookie cookie = new Cookie("RefreshTokenId", "fakeId");

        //then
        mockMvc.perform(
                post("/api/v1/auth/logout"
                ).cookie(cookie)
                    .header(AUTHORIZATION, BEARER + token.accessToken())
            )
            .andExpect(jsonPath("$.logoutResult").value(false));
    }

    @Test
    @DisplayName("[AccessToken의 사용자 정보를 반환한다.]")
    void getLoginUserInfoTest() throws Exception {
        //then
        mockMvc.perform(get("/api/v1/auth")
            .header(AUTHORIZATION, accessToken))
            .andExpect(jsonPath("$.id").value(loginUser.getId()))
            .andExpect(jsonPath("$.email").value(loginUser.getEmail()))
            .andExpect(jsonPath("$.nickname").value(loginUser.getNickname()))
            .andExpect(jsonPath("$.ageGroup").value(loginUser.getAgeGroup().getDescription()))
            .andExpect(jsonPath("$.gender").value(loginUser.getGender().getDescription()))
            .andExpect(jsonPath("$.isJoined").value(loginUser.getIsJoined()));
    }
}