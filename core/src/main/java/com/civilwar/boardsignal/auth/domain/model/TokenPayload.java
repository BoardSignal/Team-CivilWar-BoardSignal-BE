package com.civilwar.boardsignal.auth.domain.model;

import com.civilwar.boardsignal.user.domain.constants.Role;

public record TokenPayload(
    Long userId,
    Role role
) {

}
