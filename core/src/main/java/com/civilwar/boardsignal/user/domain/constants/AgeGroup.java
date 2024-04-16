package com.civilwar.boardsignal.user.domain.constants;

import static com.civilwar.boardsignal.user.exception.UserErrorCode.NOT_FOUND_AGE_GROUP;

import com.civilwar.boardsignal.common.exception.NotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeGroup {

    UNKNOWN("0", "0", "알 ㅅ 없음", 0),
    UNDER_CHILDREN("1~9", "1-9", "미취학 아동", 9),
    CHILDREN("10~14", "10-14", "어린이", 14),
    TEENAGER("14~19", "14-19", "청소년", 19),
    TWENTY("20~29", "20-29", "20대", 29),
    THIRTY("30~39", "30-39", "30대", 39),
    FORTY("40~49", "40-49", "40대", 49),
    FIFTY("50~59", "50-59", "50대", 59),
    SIXTY("60~69", "60-69", "60대", 69),
    SEVENTY("70~79", "70-79", "70대", 79),
    EIGHTY("80~89", "80-89", "80대", 89),
    NINETY("90~", "90-", "90이상", 100);

    private final String kakaoType;
    private final String naverType;
    private final String description;
    private final int maxAge;

    public static AgeGroup of(String input, String provider) {
        return Arrays.stream(values())
            .filter(ageGroup -> ageGroup.isEqual(input, provider))
            .findAny()
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_AGE_GROUP));
    }

    public static AgeGroup convert(int birthYear, LocalDateTime now) {
        AgeGroup userAgeGroup = UNKNOWN;
        int age = now.getYear() - birthYear + 1;

        for (AgeGroup a : values()) {
            //현재 연령대의 사용자의 나이가 포함된다면
            if (a.getMaxAge() >= age) {
                userAgeGroup = a;
            }
        }
        return userAgeGroup;

    }

    private boolean isEqual(String input, String provider) {
        if (provider.equals(OAuthProvider.KAKAO.getType())) {
            return input.equalsIgnoreCase(this.kakaoType);
        } else {
            return input.equalsIgnoreCase(this.naverType);
        }
    }
}
