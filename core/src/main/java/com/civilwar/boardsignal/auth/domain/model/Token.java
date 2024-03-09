package com.civilwar.boardsignal.auth.domain.model;

import com.civilwar.boardsignal.user.domain.constants.Role;

public record Token(
    String accessToken,
    String refreshTokenId,
    Role role
) {

}
