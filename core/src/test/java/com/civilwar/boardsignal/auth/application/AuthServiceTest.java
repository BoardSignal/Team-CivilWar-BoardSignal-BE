package com.civilwar.boardsignal.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.civilwar.boardsignal.auth.domain.TokenProvider;
import com.civilwar.boardsignal.auth.domain.model.Token;
import com.civilwar.boardsignal.auth.dto.request.UserLoginRequest;
import com.civilwar.boardsignal.auth.dto.response.IssueTokenResponse;
import com.civilwar.boardsignal.auth.dto.response.UserLoginResponse;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.constants.AgeGroup;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.constants.OAuthProvider;
import com.civilwar.boardsignal.user.domain.constants.Role;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AuthService authService;

    private Long id = 1L;
    private String providerId = "435432";

    @Test
    @DisplayName("[DB에 저장되어 있지 않은 OAuth 사용자 로그인 시, 데이터 저장 후 토큰 발급]")
    void loginTest1() {
        //given
        UserLoginRequest userLoginRequest = new UserLoginRequest(
            "abc1234@gmail.com",
            "최인준",
            "injuning",
            "testURL",
            String.valueOf(2000),
            AgeGroup.TWENTY.getKakaoType(),
            Gender.MALE.getKakaoType(),
            OAuthProvider.KAKAO.getType(),
            providerId
        );
        User userFixture = UserFixture.getUserFixture(
            providerId,
            Role.USER.getRole()
        );
        Token testToken = new Token("accessToken", "refreshToken", Role.USER);
        ReflectionTestUtils.setField(userFixture, "id", id);

        when(userRepository.existsUserByProviderId(providerId)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(userFixture);
        when(tokenProvider.createToken(id, Role.USER)).thenReturn(testToken);

        //when
        UserLoginResponse userLoginResponse = authService.login(userLoginRequest);

        //then
        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(0)).findByProviderId(any());
        assertThat(userLoginResponse.token().accessToken()).isEqualTo(testToken.accessToken());
    }

    @Test
    @DisplayName("[DB에 저장된 OAuth 사용자 로그인 시, 토큰 발급]")
    void loginTest2() {
        //given
        UserLoginRequest userLoginRequest = new UserLoginRequest(
            "abc1234@gmail.com",
            "최인준",
            "injuning",
            "testURL",
            String.valueOf(2000),
            AgeGroup.TWENTY.getKakaoType(),
            Gender.MALE.getKakaoType(),
            OAuthProvider.KAKAO.getType(),
            providerId
        );
        User userFixture = UserFixture.getUserFixture(
            providerId,
            Role.USER.getRole()
        );
        Token testToken = new Token("accessToken", "refreshToken", Role.USER);
        ReflectionTestUtils.setField(userFixture, "id", id);

        when(userRepository.existsUserByProviderId(providerId)).thenReturn(true);
        when(userRepository.findByProviderId(userLoginRequest.providerId())).thenReturn(
            Optional.of(userFixture));
        when(tokenProvider.createToken(id, Role.USER)).thenReturn(testToken);

        //when
        UserLoginResponse userLoginResponse = authService.login(userLoginRequest);

        //then
        verify(userRepository, times(0)).save(any());
        verify(userRepository, times(1)).findByProviderId(any());
        assertThat(userLoginResponse.token().accessToken()).isEqualTo(testToken.accessToken());
    }

    @Test
    @DisplayName("[RefreshToken을 통해 AccessToken을 재발급]")
    void IssueTokenResponse() {
        //given
        String refreshTokenId = UUID.randomUUID().toString();
        String refreshToken = "TestRefreshToken";

        when(tokenProvider.issueAccessToken(refreshTokenId)).thenReturn(refreshToken);

        //when
        IssueTokenResponse issueTokenResponse = authService.issueAccessToken(refreshTokenId);

        //then
        assertThat(issueTokenResponse.accessToken()).isEqualTo(refreshToken);
    }
}