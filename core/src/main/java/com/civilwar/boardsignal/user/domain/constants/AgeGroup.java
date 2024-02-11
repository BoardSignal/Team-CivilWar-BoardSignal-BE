package com.civilwar.boardsignal.user.domain.constants;

import static com.civilwar.boardsignal.user.exception.UserErrorCode.NOT_FOUND_AGE_GROUP;

import com.civilwar.boardsignal.common.exception.NotFoundException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeGroup {

    UNDER_CHILDREN("1~9", "미취학 아동"),
    CHILDREN("10~14", "어린이"),
    TEENAGER("14~19", "청소년"),
    TWENTY("20~29", "20대"),
    THIRTY("30~39", "30대"),
    FORTY("40~49", "40대"),
    FIFTY("50~59", "50대"),
    SIXTY("60~69", "60대"),
    SEVENTY("70~79", "70대"),
    EIGHTY("80~89", "80대"),
    NINETY("90~", "90이상");

    public static AgeGroup of(String input) {
        return Arrays.stream(values())
            .filter(ageGroup -> ageGroup.isEqual(input))
            .findAny()
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_AGE_GROUP));
    }

    private boolean isEqual(String input) {
        return input.equalsIgnoreCase(this.type);
    }

    private final String type;
    private final String description;
}
