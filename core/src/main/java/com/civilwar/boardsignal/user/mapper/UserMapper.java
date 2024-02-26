package com.civilwar.boardsignal.user.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.civilwar.boardsignal.user.domain.entity.User;
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
        int wishCount
    ) {
        return new UserProfileResponse(
            user.getNickname(),
            user.getSignal(),
            user.getUserCategories().stream()
                .map(userCategory -> userCategory.getCategory().getDescription())
                .toList(),
            user.getGender().getDescription(),
            user.getAgeGroup().getDescription(),
            user.getProfileImageUrl(),
            user.getMannerScore(),
            reviews,
            wishCount
        );
    }

}
