package com.civilwar.boardsignal.user.domain.repository;

import com.civilwar.boardsignal.user.domain.entity.UserFcmToken;
import java.util.Optional;

public interface UserFcmTokenRepository {

    UserFcmToken save(UserFcmToken userFcmToken);

    Optional<UserFcmToken> findByToken(String token);

}
