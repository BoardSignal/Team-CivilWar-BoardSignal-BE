package com.civilwar.boardsignal.user.application;

import static com.civilwar.boardsignal.user.exception.UserErrorCode.NOT_FOUND_BY_PROVIDER_ID;

import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import com.civilwar.boardsignal.user.dto.UserMapper;
import com.civilwar.boardsignal.user.dto.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getUserProfileInfo(String providerId) {
        User user = userRepository.findByProviderId(providerId)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_BY_PROVIDER_ID));

        return UserMapper.toUserProfileResponse(user);
    }
}
