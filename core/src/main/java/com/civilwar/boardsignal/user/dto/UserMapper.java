package com.civilwar.boardsignal.user.dto;

import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.dto.response.UserProfileResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static UserProfileResponse toUserProfileResponse(User user) {
        List<String> preferCategories = user.getCategories().stream()
            .map(category -> category.getCategory().getDescription())
            .toList();

        return new UserProfileResponse(
            user.getNickname(),
            user.getSignal(),
            preferCategories,
            user.getGender().getDescription(),
            user.getAgeGroup().getDescription(),
            user.getProfileImageUrl(),
            user.getMannerScore()
        );
    }
}
