package com.civilwar.boardsignal.auth.mapper;

import com.civilwar.boardsignal.auth.domain.model.Token;
import com.civilwar.boardsignal.auth.dto.response.IssueTokenResponse;
import com.civilwar.boardsignal.auth.dto.response.UserLoginResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthMapper {

    public static UserLoginResponse toUserLoginResponse(Boolean isJoined, Token token) {
        return new UserLoginResponse(isJoined, token);
    }

    public static IssueTokenResponse toIssueTokenResponse(String accessToken) {
        return new IssueTokenResponse(accessToken);
    }

}
