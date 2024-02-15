package com.civilwar.boardsignal.auth.handler;

import com.civilwar.boardsignal.auth.application.AuthService;
import com.civilwar.boardsignal.auth.dto.OAuthUserInfo;
import com.civilwar.boardsignal.auth.dto.request.UserLoginRequest;
import com.civilwar.boardsignal.auth.dto.response.ApiUserLoginResponse;
import com.civilwar.boardsignal.auth.dto.response.UserLoginResponse;
import com.civilwar.boardsignal.auth.mapper.AuthApiMapper;
import com.civilwar.boardsignal.auth.mapper.OAuthAttributeMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthService authService;

    private String toJson(ApiUserLoginResponse apiUserLoginResponse)
        throws JsonProcessingException {
        return objectMapper.writeValueAsString(apiUserLoginResponse);
    }

    private void sendResponse(HttpServletResponse response, UserLoginResponse userLoginResponse)
        throws IOException {

        //가입 여부 & AccessToken
        ApiUserLoginResponse apiUserLoginResponse = AuthApiMapper.toApiUserLoginResponse(
            userLoginResponse);

        //Cookie -> RefreshToken Id
        Cookie cookie = new Cookie("RefreshTokenId", userLoginResponse.token().refreshTokenId());
        String json = toJson(apiUserLoginResponse);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.addCookie(cookie);
        response.setContentLength(json.getBytes().length);
        response.getWriter().write(json);
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
            sendResponse(response, userLoginResponse);
        }
    }
}

