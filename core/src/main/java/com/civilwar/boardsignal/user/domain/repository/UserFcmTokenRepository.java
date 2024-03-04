package com.civilwar.boardsignal.user.domain.repository;

import com.civilwar.boardsignal.user.domain.entity.UserFcmToken;

public interface UserFcmTokenRepository {

    UserFcmToken save(UserFcmToken userFcmToken);

}
