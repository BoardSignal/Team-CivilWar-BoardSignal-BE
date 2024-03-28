package com.civilwar.boardsignal.user.infrastructure.adaptor;

import com.civilwar.boardsignal.user.domain.entity.UserFcmToken;
import com.civilwar.boardsignal.user.domain.repository.UserFcmTokenRepository;
import com.civilwar.boardsignal.user.infrastructure.repository.UserFcmTokenJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserFcmTokenRepositoryAdaptor implements UserFcmTokenRepository {

    private final UserFcmTokenJpaRepository userFcmTokenJpaRepository;

    @Override
    public UserFcmToken save(UserFcmToken userFcmToken) {
        return userFcmTokenJpaRepository.save(userFcmToken);
    }

    @Override
    public Optional<UserFcmToken> findByToken(String token) {
        return userFcmTokenJpaRepository.findByToken(token);
    }
}
