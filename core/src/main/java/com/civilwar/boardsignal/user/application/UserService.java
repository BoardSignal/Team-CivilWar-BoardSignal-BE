package com.civilwar.boardsignal.user.application;

import com.civilwar.boardsignal.image.domain.ImageRepository;
import com.civilwar.boardsignal.user.domain.constants.AgeGroup;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.entity.UserCategory;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import com.civilwar.boardsignal.user.dto.request.UserJoinRequest;
import com.civilwar.boardsignal.user.dto.response.UserJoinResponse;
import com.civilwar.boardsignal.user.mapper.UserCoreMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public UserJoinResponse joinUser(UserJoinRequest userJoinRequest, MultipartFile image) {

        //profileImage 저장
        String profileImageUrl = imageRepository.save(image);

        //Category -> UserCategory
        List<UserCategory> userCategories = userJoinRequest.categories()
            .stream()
            .map(UserCategory::of)
            .toList();

        //Dto -> User
        User user = User.of(
            userJoinRequest.email(),
            userJoinRequest.name(),
            userJoinRequest.nickName(),
            userJoinRequest.provider(),
            userJoinRequest.providerId(),
            userCategories,
            userJoinRequest.line(),
            userJoinRequest.station(),
            profileImageUrl,
            userJoinRequest.birth(),
            AgeGroup.of(userJoinRequest.ageGroup()),
            Gender.of(userJoinRequest.gender())
        );

        //연관 관계 매핑
        user.updateCategories(userCategories);

        User savedUser = userRepository.save(user);
        return UserCoreMapper.of(savedUser);
    }

}
