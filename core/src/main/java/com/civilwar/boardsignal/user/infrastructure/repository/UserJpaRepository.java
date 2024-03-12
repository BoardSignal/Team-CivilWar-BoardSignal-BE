package com.civilwar.boardsignal.user.infrastructure.repository;

import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderId(String providerId);

    boolean existsUserByProviderId(String providerId);

    @Query("select u from User u where u.id in :ids")
    List<User> findAllInIds(@Param("ids") List<Long> ids);

    Optional<User> findByNicknameAndIsJoined(String nickname, Boolean isJoined);
}
