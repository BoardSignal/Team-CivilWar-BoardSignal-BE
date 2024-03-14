package com.civilwar.boardsignal.user.infrastructure.adaptor;

import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import com.civilwar.boardsignal.user.infrastructure.repository.UserJpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdaptor implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public void saveAll(Collection<User> users) {
        userJpaRepository.saveAll(users);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }

    @Override
    public Optional<User> findByProviderId(String providerId) {
        return userJpaRepository.findByProviderId(providerId);
    }

    @Override
    public boolean existsUserByProviderId(String providerId) {
        return userJpaRepository.existsUserByProviderId(providerId);
    }

    @Override
    public List<User> findAllInIds(List<Long> ids) {
        return userJpaRepository.findAllInIds(ids);
    }

    @Override
    public Optional<User> findByNicknameAndIsJoined(String nickname, Boolean isJoined) {
        return userJpaRepository.findByNicknameAndIsJoined(nickname, isJoined);
    }

    @Override
    public List<User> findByStation(String station) {
        return userJpaRepository.findByStation(station);
    }
}
