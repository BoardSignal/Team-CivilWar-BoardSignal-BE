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
        String nickName = (String) profile.get("nickname");
        String imageUrl = (String) profile.get("profile_image_url");
        String providerId = userAttributes.get("id").toString();

        return new OAuthUserInfo(
            email,
            nickName,
            imageUrl,
            provider,
            providerId
        );
    }

    private static OAuthUserInfo naverUserInfo(Map<String, Object> userAttributes,
        String provider) {

        Map<String, String> naverAccount = (Map<String, String>) userAttributes
            .get("response");

        String email = naverAccount.get("email");
        String nickName = naverAccount.get("nickname");
        String imageUrl = naverAccount.get("profile_image");
        String providerId = naverAccount.get("id");

        return new OAuthUserInfo(
            email,
            nickName,
            imageUrl,
            provider,
            providerId
        );
    }

}
