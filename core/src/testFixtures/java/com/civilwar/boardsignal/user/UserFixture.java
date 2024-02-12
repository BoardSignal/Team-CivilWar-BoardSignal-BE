package com.civilwar.boardsignal.user;

import static lombok.AccessLevel.PRIVATE;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.user.domain.constants.AgeGroup;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class UserFixture {

    public static User getUserFixture(String providerId, String imageUrl) {

        List<Category> categories = List.of(Category.FAMILY, Category.PARTY);

        return User.of(
            "abc1234@gmail.com",
            "최인준",
            "injuning",
            "kakao",
            providerId,
            categories,
            "2호선",
            "사당역",
            imageUrl,
            2000,
            AgeGroup.of("20~29"),
            Gender.of("male")
        );
    }

}
