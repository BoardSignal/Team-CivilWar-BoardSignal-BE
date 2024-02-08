package com.civilwar.boardsignal.auth.domain;

import com.civilwar.boardsignal.auth.domain.model.Token;
import com.civilwar.boardsignal.auth.domain.model.TokenPayload;
import com.civilwar.boardsignal.user.domain.constants.Role;

public interface TokenProvider {

    Token createToken(Long id, Role role);

    TokenPayload getPayLoad(String token);

    void validateToken(String token);
}
