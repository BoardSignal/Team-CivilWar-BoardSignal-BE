package com.civilwar.boardsignal.auth.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.civilwar.boardsignal.auth.domain.model.Token;
import com.civilwar.boardsignal.auth.domain.model.TokenPayload;
import com.civilwar.boardsignal.auth.exception.AuthErrorCode;
import com.civilwar.boardsignal.auth.properties.JwtProperty;
import com.civilwar.boardsignal.common.exception.ValidationException;
import com.civilwar.boardsignal.user.domain.constants.Role;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[JwtTokenProvider 테스트]")
class JwtTokenProviderTest {

    private final String clientSecret = "FLGs0worlfbOS8CEdfSPW04mb0dkD9SKFlsoa9WK9wW0WkdlskYof514263jdmsk";
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtProperty jwtProperty;
    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private Long id;
    private String refreshTokenId;

    @Test
    @DisplayName("[Jwt 토큰 생성]")
    void createTokenTest() {
        //given
        id = 1L;
        refreshTokenId = UUID.randomUUID().toString();
        when(jwtProperty.getClientSecret()).thenReturn(clientSecret);
        when(jwtProperty.getAccessExpiryTime()).thenReturn(1L);
        when(jwtProperty.getRefreshExpiryTime()).thenReturn(2L);

        //when
        Token token = jwtTokenProvider.createToken(id, Role.USER);
        TokenPayload payLoad = jwtTokenProvider.getPayLoad(token.accessToken());

        //then
        assertThat(payLoad.userId()).isEqualTo(id);
    }

    @Test
    @DisplayName("[RefreshToken을 통해 AccessToken을 재발급 한다]")
    void createAccessToken() {
        //given
        id = 1L;
        when(jwtProperty.getClientSecret()).thenReturn(clientSecret);
        when(jwtProperty.getAccessExpiryTime()).thenReturn(1L);
        when(jwtProperty.getRefreshExpiryTime()).thenReturn(2L);
        Token token = jwtTokenProvider.createToken(id, Role.USER);
        when(refreshTokenRepository.findById(refreshTokenId)).thenReturn(
            Optional.of(token.accessToken()));

        //when
        String accessToken = jwtTokenProvider.issueAccessToken(refreshTokenId);
        TokenPayload payLoad = jwtTokenProvider.getPayLoad(accessToken);

        //then
        assertThat(payLoad.userId()).isEqualTo(id);
    }

    @Test
    @DisplayName("[토큰 만료시 예외가 발생한다.]")
    void validateToken() {
        //given
        Long id = 1L;
        when(jwtProperty.getClientSecret()).thenReturn(clientSecret);
        when(jwtProperty.getAccessExpiryTime()).thenReturn(0L);
        when(jwtProperty.getRefreshExpiryTime()).thenReturn(2L);

        //when
        Token token = jwtTokenProvider.createToken(id, Role.USER);

        //then
        assertThatThrownBy(() -> jwtTokenProvider.validateToken(token.accessToken()))
            .isInstanceOf(ValidationException.class)
            .hasMessage(AuthErrorCode.AUTH_TOKEN_EXPIRED.getMessage());
    }
}