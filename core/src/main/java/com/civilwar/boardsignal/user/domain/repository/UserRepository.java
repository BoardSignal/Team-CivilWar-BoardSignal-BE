package com.civilwar.boardsignal.user.domain.repository;

import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    void saveAll(Collection<User> users);

    Optional<User> findById(Long id);

    Optional<User> findUserWithCategoryById(Long id);

    List<User> findAll();

    Optional<User> findByProviderId(String providerId);

    boolean existsUserByProviderId(String providerId);

    List<User> findAllInIds(List<Long> ids);

    Optional<User> findByNicknameAndIsJoined(String nickname, Boolean isJoined);

    List<User> findByStation(String station);
}
