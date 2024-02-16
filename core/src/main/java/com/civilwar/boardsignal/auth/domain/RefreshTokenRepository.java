package com.civilwar.boardsignal.auth.domain;

import java.util.Optional;

public interface RefreshTokenRepository {

    void save(String id, String refreshToken, Long refreshExpiryTime);

    Optional<String> findById(String id);

    Boolean delete(String id);

}
