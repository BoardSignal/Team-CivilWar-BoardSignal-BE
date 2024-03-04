package com.civilwar.boardsignal.user.application;

import com.civilwar.boardsignal.boardgame.domain.repository.WishRepository;
import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.image.domain.ImageRepository;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import com.civilwar.boardsignal.user.dto.request.UserModifyRequest;
import com.civilwar.boardsignal.user.dto.response.UserModifyResponse;
import com.civilwar.boardsignal.user.dto.response.UserProfileResponse;
import com.civilwar.boardsignal.user.dto.response.UserReviewResponse;
import com.civilwar.boardsignal.user.exception.UserErrorCode;
import com.civilwar.boardsignal.user.facade.UserReviewFacade;
import com.civilwar.boardsignal.user.mapper.UserMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WishRepository wishRepository;
    private final ImageRepository imageRepository;
    private final UserReviewFacade userReviewFacade;

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

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileInfo(Long profileUserId, User loginUser) {
        //유저 정보 조회
        User profileUser = userRepository.findById(profileUserId)
            .orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND_USER));

        //찜 갯수 조회
        int wishCount = wishRepository.countWishByUserId(profileUserId);

        //유저와 관련된 리뷰 정보 조회
        List<UserReviewResponse> userReviews = userReviewFacade.getUserReview(profileUserId);

        //프로필 방문 유저가 해당 프로필 주인인지 확인
        Boolean isProfileManager = profileUserId.equals(loginUser.getId());

        return UserMapper.toUserProfileResponse(profileUser, userReviews, wishCount,
            isProfileManager);
    }

}
