package com.civilwar.boardsignal.user.application;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.boardgame.domain.repository.WishRepository;
import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.image.domain.ImageRepository;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.entity.UserCategory;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import com.civilwar.boardsignal.user.dto.request.UserModifyRequest;
import com.civilwar.boardsignal.user.dto.request.ValidNicknameRequest;
import com.civilwar.boardsignal.user.dto.response.LoginUserInfoResponse;
import com.civilwar.boardsignal.user.dto.response.UserModifyResponse;
import com.civilwar.boardsignal.user.dto.response.UserProfileResponse;
import com.civilwar.boardsignal.user.dto.response.UserReviewResponse;
import com.civilwar.boardsignal.user.dto.response.ValidNicknameResponse;
import com.civilwar.boardsignal.user.exception.UserErrorCode;
import com.civilwar.boardsignal.user.facade.UserReviewFacade;
import com.civilwar.boardsignal.user.mapper.UserMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final WishRepository wishRepository;
    private final ImageRepository imageRepository;
    private final UserReviewFacade userReviewFacade;
    private final Supplier<LocalDateTime> now;

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

        //내가 참여한 모든 fixRoom
        List<Room> myFixGame = roomRepository.findMyFixRoom(loginUser.getId());

        //내가 이전에 참여한 모임 조회 갯수
        long endGameCount = myFixGame.stream()
            .filter(room -> room.getMeetingInfo().getMeetingTime().toLocalDate()
                .isBefore(now.get().toLocalDate())
            ).count();

        return UserMapper.toUserProfileResponse(profileUser, userReviews, wishCount,
            isProfileManager, (int) endGameCount);
    }

    @Transactional(readOnly = true)
    public ValidNicknameResponse isExistNickname(ValidNicknameRequest validNicknameRequest,
        User loginUser) {
        Boolean isNotValid;

        //검증 닉네임을 가진, 회원가입(true)된 사용자 조회
        Optional<User> optionalUser = userRepository.findByNicknameAndIsJoined(
            validNicknameRequest.nickname(), true);

        //존재하지 않는다면 - 중복 X (true) 반환
        if (optionalUser.isEmpty()) {
            isNotValid = true;
        } else {
            //검증 닉네임이 회원이 자신이라면 - 중복 X (true) 반환
            //검증 닉네임이 타인이라면 - 중복 O (false) 반환
            User findUser = optionalUser.get();
            isNotValid = findUser.getId().equals(loginUser.getId());
        }

        return new ValidNicknameResponse(isNotValid);
    }

    @Transactional(readOnly = true)
    public List<User> getUserByStation(String station) {
        return userRepository.findByStation(station);
    }

    @Transactional(readOnly = true)
    public LoginUserInfoResponse getLoginUserInfo(User loginUser) {
        User loginUserEntity = userRepository.findById(loginUser.getId())
            .orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND_USER));

        int currentYear = now.get().getYear();
        int birthYear = loginUserEntity.getBirth();
        //로그인 유저 나이
        int myAge = currentYear - birthYear + 1;

        List<Category> categories = loginUserEntity.getUserCategories()
            .stream()
            .map(UserCategory::getCategory)
            .toList();

        return UserMapper.toLoginUserInfoResponse(loginUserEntity, myAge, categories);
    }

}
