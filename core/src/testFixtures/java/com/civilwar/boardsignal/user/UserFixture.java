package com.civilwar.boardsignal.user;

import static lombok.AccessLevel.PRIVATE;

import com.civilwar.boardsignal.user.domain.constants.AgeGroup;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.constants.OAuthProvider;
import com.civilwar.boardsignal.user.domain.entity.User;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class UserFixture {

    public static User getUserFixture(String providerId, String imageUrl) {

        String provider = OAuthProvider.KAKAO.getType();
        return User.of(
            "abc1234@gmail.com",
            "최인준",
            "injuning",
            OAuthProvider.KAKAO.getType(),
            providerId,
            imageUrl,
            2000,
            AgeGroup.of("20~29", provider),
            Gender.of("male", provider)
        );
    }

    public static User getUserFixture2(String providerId, String imageUrl) {

        String provider = OAuthProvider.KAKAO.getType();
        return User.of(
            "abc12345678@naver.com",
            "김강훈",
            "macbook",
            OAuthProvider.KAKAO.getType(),
            providerId,
            imageUrl,
            2000,
            AgeGroup.of("20~29", provider),
            Gender.of("male", provider)
        );
    }

}
