package com.civilwar.boardsignal.user.application;

import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.image.domain.ImageRepository;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import com.civilwar.boardsignal.user.dto.request.UserModifyRequest;
import com.civilwar.boardsignal.user.dto.response.UserModifyResponse;
import com.civilwar.boardsignal.user.exception.UserErrorCode;
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
    public UserModifyResponse modifyUser(UserModifyRequest userModifyRequest) {

        //profileImage 저장
        String profileImageUrl = imageRepository.save(userModifyRequest.image());

        //회원 정보 수정
        User findUser = userRepository.findById(userModifyRequest.id())
            .orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND_USER));

        findUser.updateUser(
            userModifyRequest.nickName(),
            userModifyRequest.categories(),
            userModifyRequest.line(),
            userModifyRequest.station(),
            profileImageUrl
        );

        return UserMapper.toUserModifyResponse(findUser);
    }

}
