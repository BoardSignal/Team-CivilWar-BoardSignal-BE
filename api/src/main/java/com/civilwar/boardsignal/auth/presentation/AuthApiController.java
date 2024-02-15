package com.civilwar.boardsignal.auth.presentation;

import com.civilwar.boardsignal.auth.application.AuthService;
import com.civilwar.boardsignal.auth.dto.response.IssueTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Auth API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    private final AuthService authService;

    @Operation(summary = "카카오 로그인 API", description = "웹 페이지를 통한 로그인")
    @PostMapping("/login/kakao")
    public ResponseEntity<?> kakaoLoginRedirect() {
        HttpHeaders httpHeaders = new HttpHeaders();
        //카카오 로그인 페이지 리다이렉트
        httpHeaders.setLocation(URI.create("/oauth2/authorization/kakao"));
        return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
    }

    @Operation(summary = "네이버 로그인 API", description = "웹 페이지를 통한 로그인")
    @PostMapping("/login/naver")
    public ResponseEntity<?> naverLoginRedirect() {
        HttpHeaders httpHeaders = new HttpHeaders();
        //네이버 로그인 페이지 리다이렉트
        httpHeaders.setLocation(URI.create("/oauth2/authorization/naver"));
        return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
    }

    @Operation(summary = "AccessToken 재발급 API", description = "Cookie에 RefreshToken Id 필요")
    @GetMapping("/reissue")
    public ResponseEntity<IssueTokenResponse> issueAccessToken(
        @CookieValue(name = "RefreshTokenId") String refreshTokenId
    ) {
        return ResponseEntity.ok(authService.issueAccessToken(refreshTokenId));
    }

}
