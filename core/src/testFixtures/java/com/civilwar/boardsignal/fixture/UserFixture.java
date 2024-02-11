package com.civilwar.boardsignal.fixture;

import static com.civilwar.boardsignal.boardgame.domain.constant.Category.CUSTOMIZABLE;
import static com.civilwar.boardsignal.boardgame.domain.constant.Category.WAR;

import com.civilwar.boardsignal.user.domain.constants.AgeGroup;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserFixture {

    public static User getUser() {
        return User.of(
            "hello123@email.com",
            "userA",
            "helloA",
            "kakao",
            "providerId123",
            List.of(CUSTOMIZABLE, WAR),
            "서울시 강남구",
            "profileImageUrl",
            1999,
            AgeGroup.TWENTY,
            Gender.MALE
        );
    }
}
