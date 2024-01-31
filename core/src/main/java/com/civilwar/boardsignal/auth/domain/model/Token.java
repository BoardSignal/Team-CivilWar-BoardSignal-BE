package com.civilwar.boardsignal.auth.domain.model;

import com.civilwar.boardsignal.user.domain.entity.Role;

public record Token(
    String accessToken,
    String refreshToken,
    Role role
) {

}
