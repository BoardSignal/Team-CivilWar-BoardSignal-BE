package com.civilwar.boardsignal.user.domain.constants;

import static com.civilwar.boardsignal.user.exception.UserErrorCode.NOT_FOUND_GENDER;

import com.civilwar.boardsignal.common.exception.NotFoundException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {

    MALE("male", "M", "남성"),
    FEMALE("female", "F", "여성"),
    UNION(null, null, "혼성");

    private final String kakaoType;
    private final String naverType;
    private final String description;

    public static Gender of(String input, String provider) {
        return Arrays.stream(values())
            .filter(gender -> gender.isEqual(input, provider))
            .findAny()
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_GENDER));
    }

    private boolean isEqual(String input, String provider) {
        if (provider.equals(OAuthProvider.KAKAO.getType())) {
            return input.equalsIgnoreCase(this.kakaoType);
        } else {
            return input.equalsIgnoreCase(this.naverType);
        }
    }

    public static Gender toGender(String input) {
        return Arrays.stream(values())
            .filter(gender -> gender.isEqual(input))
            .findAny()
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_GENDER));
    }

    private boolean isEqual(String input) {
        return input.equalsIgnoreCase(this.description);
    }
}
