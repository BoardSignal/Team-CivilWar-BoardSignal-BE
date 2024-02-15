package com.civilwar.boardsignal.user.application;

import com.civilwar.boardsignal.image.domain.ImageRepository;
import com.civilwar.boardsignal.user.domain.constants.AgeGroup;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import com.civilwar.boardsignal.user.dto.request.UserJoinRequest;
import com.civilwar.boardsignal.user.dto.response.UserJoinResponse;
import com.civilwar.boardsignal.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public UserJoinResponse joinUser(UserJoinRequest userJoinRequest) {

        //profileImage 저장
        String profileImageUrl = imageRepository.save(userJoinRequest.image());

        //Dto -> User
        User user = User.of(
            userJoinRequest.email(),
            userJoinRequest.name(),
            userJoinRequest.nickName(),
            userJoinRequest.provider(),
            userJoinRequest.providerId(),
            profileImageUrl,
            userJoinRequest.birth(),
            AgeGroup.of(userJoinRequest.ageGroup(), userJoinRequest.provider()),
            Gender.of(userJoinRequest.gender(), userJoinRequest.provider())
        );

        userRepository.save(user);
        return UserMapper.toUserJoinResponse(user);
    }

}
