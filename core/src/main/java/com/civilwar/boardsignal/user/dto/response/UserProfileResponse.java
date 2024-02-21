package com.civilwar.boardsignal.user.dto.response;

import java.util.List;

public record UserProfileResponse(
    String nickname,
    int signal,
    List<String> preferCategories,
    String gender,
    String ageGroup,
    String profileImageUrl,
    double mannerScore,
    List<UserReviewResponse> reviews,
    int wishCount
) {

}
