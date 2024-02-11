package com.civilwar.boardsignal.user;

import static lombok.AccessLevel.PRIVATE;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.user.domain.constants.AgeGroup;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.entity.UserCategory;
import com.civilwar.boardsignal.user.dto.request.UserJoinRequest;
import java.util.List;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class UserFixture {

    public static UserJoinRequest getUserJoinRequest(String providerId) {

        return new UserJoinRequest(
            "abc1234@gmail.com",
            "최인준",
            "injuning",
            "kakao",
            providerId,
            List.of(Category.FAMILY, Category.PARTY),
            "2호선",
            "사당역",
            2000,
            "20~29",
            "male"
        );
    }

    public static User getUserFixture(String providerId, String imageUrl) {

        List<UserCategory> userCategories = Stream.of(Category.FAMILY, Category.PARTY)
            .map(UserCategory::of)
            .toList();

        return User.of(
            "abc1234@gmail.com",
            "최인준",
            "injuning",
            "kakao",
            providerId,
            userCategories,
            "2호선",
            "사당역",
            imageUrl,
            2000,
            AgeGroup.of("20~29"),
            Gender.of("male")
        );
    }

}
