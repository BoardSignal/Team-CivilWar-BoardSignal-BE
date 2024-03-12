package com.civilwar.boardsignal.auth.mapper;

import com.civilwar.boardsignal.auth.domain.model.Token;
import com.civilwar.boardsignal.auth.dto.OAuthUserInfo;
import com.civilwar.boardsignal.auth.dto.request.UserLoginRequest;
import com.civilwar.boardsignal.auth.dto.response.ApiUserLoginResponse;
import com.civilwar.boardsignal.auth.dto.response.LoginUserInfoResponse;
import com.civilwar.boardsignal.auth.dto.response.UserLoginResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthApiMapper {

    public static UserLoginRequest toUserLoginRequest(OAuthUserInfo oAuthUserInfo) {
        return new UserLoginRequest(
            oAuthUserInfo.email(),
            oAuthUserInfo.name(),
            oAuthUserInfo.nickname(),
            oAuthUserInfo.imageUrl(),
            oAuthUserInfo.birthYear(),
            oAuthUserInfo.ageRange(),
            oAuthUserInfo.gender(),
            oAuthUserInfo.provider(),
            oAuthUserInfo.providerId()
        );
    }

    public static ApiUserLoginResponse toApiUserLoginResponse(UserLoginResponse userLoginResponse) {
        Token token = userLoginResponse.token();
        return new ApiUserLoginResponse(userLoginResponse.isJoined(), token.accessToken());
    }

    public static LoginUserInfoResponse toLoginUserInfoResponse(
        Long id,
        String email,
        String name,
        String nickname,
        int birth,
        int age,
        String ageGroup,
        String gender,
        Boolean isJoined
    ) {
        return new LoginUserInfoResponse(
            id,
            email,
            name,
            nickname,
            birth,
            age,
            ageGroup,
            gender,
            isJoined
        );
    }

}
