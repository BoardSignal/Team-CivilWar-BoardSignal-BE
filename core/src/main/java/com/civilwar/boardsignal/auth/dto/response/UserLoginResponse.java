package com.civilwar.boardsignal.auth.dto.response;

import com.civilwar.boardsignal.auth.domain.model.Token;

public record UserLoginResponse(
    boolean isJoined,
    Token token
) {

}
