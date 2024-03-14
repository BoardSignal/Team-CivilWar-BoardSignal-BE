package com.civilwar.boardsignal.auth.presentation;

import com.civilwar.boardsignal.auth.application.AuthService;
import com.civilwar.boardsignal.auth.dto.response.IssueTokenResponse;
import com.civilwar.boardsignal.auth.dto.response.UserLogoutResponse;
import com.civilwar.boardsignal.user.application.UserService;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.dto.response.LoginUserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private static final String REFRESHTOKEN_NAME = "RefreshToken_Id";
    private final AuthService authService;
    private final UserService userService;

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
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/reissue")
    public ResponseEntity<IssueTokenResponse> issueAccessToken(
        HttpServletRequest request
    ) {
        String refreshTokenId = null;

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(REFRESHTOKEN_NAME)) {
                refreshTokenId = cookie.getValue();
            }
        }

        return ResponseEntity.ok(authService.issueAccessToken(refreshTokenId));
    }

    @Operation(summary = "로그아웃 API", description = "Cookie에 RefreshToken Id 필요")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/logout")
    public ResponseEntity<UserLogoutResponse> logout(
        @CookieValue(name = REFRESHTOKEN_NAME) String refreshTokenId,
        HttpServletResponse response
    ) {
        //쿠키 제거
        Cookie cookie = new Cookie(REFRESHTOKEN_NAME, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
        //로그아웃 성공 시 true 반환
        return ResponseEntity.ok(authService.logout(refreshTokenId));
    }

    @Operation(summary = "현재 로그인 한 사용자 정보 확인 API")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping
    public ResponseEntity<LoginUserInfoResponse> getLoginUserInfo(
        @AuthenticationPrincipal User loginUser) {

        LoginUserInfoResponse loginUserInfo = userService.getLoginUserInfo(loginUser);

        return ResponseEntity.ok(loginUserInfo);
    }

}
