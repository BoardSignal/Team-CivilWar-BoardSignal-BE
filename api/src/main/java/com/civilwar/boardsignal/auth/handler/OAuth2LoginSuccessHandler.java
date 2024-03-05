package com.civilwar.boardsignal.auth.handler;

import com.civilwar.boardsignal.auth.application.AuthService;
import com.civilwar.boardsignal.auth.dto.OAuthUserInfo;
import com.civilwar.boardsignal.auth.dto.request.UserLoginRequest;
import com.civilwar.boardsignal.auth.dto.response.ApiUserLoginResponse;
import com.civilwar.boardsignal.auth.dto.response.UserLoginResponse;
import com.civilwar.boardsignal.auth.mapper.AuthApiMapper;
import com.civilwar.boardsignal.auth.mapper.OAuthAttributeMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final String DOMAIN = "http://localhost:5173";

    private final AuthService authService;

    private void sendResponse(HttpServletRequest request, HttpServletResponse response,
        UserLoginResponse userLoginResponse)
        throws IOException {

        //가입 여부 & AccessToken
        ApiUserLoginResponse apiUserLoginResponse = AuthApiMapper.toApiUserLoginResponse(
            userLoginResponse);

        //Cookie -> RefreshToken Id
        Cookie cookie = new Cookie("TestRefreshToken", userLoginResponse.token().refreshTokenId());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(43200);   //임시 값 -> 12시간
        response.addCookie(cookie);

        //Redirect URL 생성
        String url = UriComponentsBuilder.fromUriString(DOMAIN + "/redirect")
            .queryParam("access-token", apiUserLoginResponse.accessToken())
            .queryParam("is-joined", apiUserLoginResponse.isJoined())
            .build()
            .toUri()
            .toString();

        //프론트 Redirect
        getRedirectStrategy().sendRedirect(request, response, url);
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {

        log.info("OAuth Login Success!!");

        if (authentication instanceof OAuth2AuthenticationToken authenticationToken) {

            //1. OAuth 로그인 유저 정보
            Map<String, Object> userAttributes = authenticationToken.getPrincipal().getAttributes();
            String provider = authenticationToken.getAuthorizedClientRegistrationId();

            //2. Provider 구별하여 필요한 정보 매핑
            OAuthUserInfo oAuthUserInfo = OAuthAttributeMapper.toOAuthUserInfo(userAttributes,
                provider);

            //3. 로그인 처리
            UserLoginRequest userLoginRequest = AuthApiMapper.toUserLoginRequest(oAuthUserInfo);
            UserLoginResponse userLoginResponse = authService.login(userLoginRequest);

            //4. 응답
            sendResponse(request, response, userLoginResponse);
        }
    }
}

