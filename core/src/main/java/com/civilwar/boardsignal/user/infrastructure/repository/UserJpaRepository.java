package com.civilwar.boardsignal.user.infrastructure.repository;

import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderId(String providerId);

    boolean existsUserByProviderId(String providerId);
}
