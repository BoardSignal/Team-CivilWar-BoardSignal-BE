package com.civilwar.boardsignal.auth.presentation;

import com.civilwar.boardsignal.auth.application.AuthService;
import com.civilwar.boardsignal.auth.dto.response.IssueTokenResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    private final AuthService authService;

    @PostMapping("/login/kakao")
    public ResponseEntity<?> kakaoLoginRedirect() {
        HttpHeaders httpHeaders = new HttpHeaders();
        //카카오 로그인 페이지 리다이렉트
        httpHeaders.setLocation(URI.create("/oauth2/authorization/kakao"));
        return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
    }

    @PostMapping("/login/naver")
    public ResponseEntity<?> naverLoginRedirect() {
        HttpHeaders httpHeaders = new HttpHeaders();
        //네이버 로그인 페이지 리다이렉트
        httpHeaders.setLocation(URI.create("/oauth2/authorization/naver"));
        return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping("/reissue")
    public ResponseEntity<IssueTokenResponse> issueAccessToken(
        @CookieValue(name = "RefreshTokenId") String refreshTokenId
    ) {
        return ResponseEntity.ok(authService.issueAccessToken(refreshTokenId));
    }

}
