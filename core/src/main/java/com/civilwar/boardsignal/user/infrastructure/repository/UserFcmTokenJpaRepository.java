package com.civilwar.boardsignal.user.infrastructure.repository;

import com.civilwar.boardsignal.user.domain.entity.UserFcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFcmTokenJpaRepository extends JpaRepository<UserFcmToken, Long> {

}
