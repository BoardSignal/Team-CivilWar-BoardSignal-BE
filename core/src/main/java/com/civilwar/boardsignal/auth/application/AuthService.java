package com.civilwar.boardsignal.auth.application;

import com.civilwar.boardsignal.auth.domain.TokenProvider;
import com.civilwar.boardsignal.auth.domain.model.Token;
import com.civilwar.boardsignal.auth.dto.request.UserLoginRequest;
import com.civilwar.boardsignal.auth.dto.response.IssueTokenResponse;
import com.civilwar.boardsignal.auth.dto.response.UserLoginResponse;
import com.civilwar.boardsignal.auth.dto.response.UserLogoutResponse;
import com.civilwar.boardsignal.auth.mapper.AuthMapper;
import com.civilwar.boardsignal.user.domain.constants.AgeGroup;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Transactional
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {

        User user;

        //1. DB에 저장이 되어 있지 않다면 -> 회원 가입
        if (!userRepository.existsUserByProviderId(userLoginRequest.providerId())) {

            user = userRepository.save(
                User.of(
                    userLoginRequest.email(),
                    userLoginRequest.nickname(),
                    userLoginRequest.provider(),
                    userLoginRequest.providerId(),
                    userLoginRequest.imageUrl(),
                    Gender.UNKNOWN,
                    AgeGroup.UNKNOWN
                )
            );
        }
        //2. DB에 저장되어 있다면 -> user 정보 조회
        else {
            user = userRepository.findByProviderId(userLoginRequest.providerId())
                .orElseThrow();
        }

        //(가입 여부 & 토큰) 전달
        Token token = tokenProvider.createToken(user.getId(), user.getRole());
        return AuthMapper.toUserLoginResponse(user.getIsJoined(), token);
    }

    //AccessToken 재발급 로직
    public IssueTokenResponse issueAccessToken(String refreshTokenId) {

        //refreshTokenId를 통해 AccessToken 재발급
        String accessToken = tokenProvider.issueAccessToken(refreshTokenId);
        return AuthMapper.toIssueTokenResponse(accessToken);
    }

    public UserLogoutResponse logout(String refreshTokenId) {

        //refreshToken 삭제 후, boolean 응답
        return AuthMapper.toUserLogoutResponse(tokenProvider.deleteRefreshToken(refreshTokenId));
    }

}
