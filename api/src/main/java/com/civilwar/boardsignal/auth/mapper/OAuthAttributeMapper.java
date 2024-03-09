package com.civilwar.boardsignal.auth.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.civilwar.boardsignal.auth.dto.OAuthUserInfo;
import com.civilwar.boardsignal.user.domain.constants.OAuthProvider;
import java.util.Map;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class OAuthAttributeMapper {

    public static OAuthUserInfo toOAuthUserInfo(
        Map<String, Object> userAttributes,
        String provider
    ) {

        if (provider.equals(OAuthProvider.KAKAO.getType())) {
            return kakaoUserInfo(userAttributes, provider);
        } else {
            return naverUserInfo(userAttributes, provider);
        }

    }

    private static OAuthUserInfo kakaoUserInfo(Map<String, Object> userAttributes,
        String provider) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) userAttributes
            .get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String name = (String) kakaoAccount.get("name");
        String nickName = (String) profile.get("nickname");
        String imageUrl = (String) profile.get("profile_image_url");
        String birthYear = (String) kakaoAccount.get("birthyear");
        String ageRange = (String) kakaoAccount.get("age_range");
        String gender = (String) kakaoAccount.get("gender");
        String providerId = userAttributes.get("id").toString();

        return new OAuthUserInfo(
            email,
            name,
            nickName,
            imageUrl,
            birthYear,
            ageRange,
            gender,
            provider,
            providerId
        );
    }

    private static OAuthUserInfo naverUserInfo(Map<String, Object> userAttributes,
        String provider) {

        Map<String, String> kakaoAccount = (Map<String, String>) userAttributes
            .get("response");

        String email = kakaoAccount.get("email");
        String name = kakaoAccount.get("name");
        String nickName = kakaoAccount.get("nickname");
        String imageUrl = kakaoAccount.get("profile_image");
        String birthYear = kakaoAccount.get("birthyear");
        String ageRange = kakaoAccount.get("age");
        String gender = kakaoAccount.get("gender");
        String providerId = kakaoAccount.get("id");

        return new OAuthUserInfo(
            email,
            name,
            nickName,
            imageUrl,
            birthYear,
            ageRange,
            gender,
            provider,
            providerId
        );
    }

}
