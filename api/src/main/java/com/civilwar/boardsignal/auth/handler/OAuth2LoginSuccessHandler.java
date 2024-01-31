package com.civilwar.boardsignal.auth.handler;

import com.civilwar.boardsignal.auth.domain.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        log.info("OAuth Login Success!!");
        if (authentication instanceof OAuth2AuthenticationToken authenticationToken) {
            String provider = authenticationToken.getAuthorizedClientRegistrationId();
            OAuth2User oAuth2User = authenticationToken.getPrincipal();
            // todo : 회원가입 처리

            log.info("oAuthUser's role : {}", authenticationToken.getAuthorities());
            // todo : 토큰 생성 처리

            String url = UriComponentsBuilder.fromUriString("https://mydomain" + "/welcome")
//				.queryParam("access-token", accessToken)
//				.queryParam("refresh-token", refreshToken)
                .queryParam("provider", provider)
                .build()
                .toUri()
                .toString();

            getRedirectStrategy().sendRedirect(request, response, url);
        }
    }
}
