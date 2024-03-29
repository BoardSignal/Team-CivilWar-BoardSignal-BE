package com.civilwar.boardsignal.user.dto.response;

import java.util.List;

public record UserProfileResponse(
    Long profileUserId,
    String nickname,
    int signal,
    List<String> preferCategories,
    String gender,
    String ageGroup,
    String profileImageUrl,
    double signalTemperature,
    List<UserReviewResponse> reviews,
    int wishCount,
    Boolean isProfileManager,
    int endGameCount
) {

}
