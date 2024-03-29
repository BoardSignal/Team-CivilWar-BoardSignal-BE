package com.civilwar.boardsignal.user.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.dto.response.LoginUserInfoResponse;
import com.civilwar.boardsignal.user.dto.response.UserModifyResponse;
import com.civilwar.boardsignal.user.dto.response.UserProfileResponse;
import com.civilwar.boardsignal.user.dto.response.UserReviewResponse;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class UserMapper {

    public static UserModifyResponse toUserModifyResponse(User user) {
        return new UserModifyResponse(user.getId());
    }

    public static UserProfileResponse toUserProfileResponse(
        User user,
        List<UserReviewResponse> reviews,
        int wishCount,
        Boolean isProfileManager,
        int endGameCount
    ) {
        return new UserProfileResponse(
            user.getId(),
            user.getNickname(),
            user.getSignal(),
            user.getUserCategories().stream()
                .map(userCategory -> userCategory.getCategory().getDescription())
                .toList(),
            user.getGender().getDescription(),
            user.getAgeGroup().getDescription(),
            user.getProfileImageUrl(),
            (double) Math.round(user.getMannerScore() * 10) / 10,
            reviews,
            wishCount,
            isProfileManager,
            endGameCount
        );
    }

    public static LoginUserInfoResponse toLoginUserInfoResponse(
        User loginUser,
        int age,
        List<Category> categories
    ) {
        return new LoginUserInfoResponse(
            loginUser.getId(),
            loginUser.getEmail(),
            loginUser.getName(),
            loginUser.getNickname(),
            loginUser.getBirth(),
            age,
            loginUser.getAgeGroup().getDescription(),
            loginUser.getGender().getDescription(),
            loginUser.getIsJoined(),
            loginUser.getLine(),
            loginUser.getStation(),
            categories.stream()
                .map(Category::getDescription)
                .toList(),
            loginUser.getProfileImageUrl()
        );
    }

}
