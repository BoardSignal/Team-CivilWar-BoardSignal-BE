package com.civilwar.boardsignal.user.domain.constants;

import static com.civilwar.boardsignal.common.exception.ValidationErrorCode.TYPE_VALIDATED;

import com.civilwar.boardsignal.common.exception.ValidationException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeGroup {

    UNDER_CHILDREN("1~9"),
    CHILDREN("10~14"),
    TEENAGER("14~19"),
    TWENTY("20~29"),
    THIRTY("30~39"),
    FORTY("40~49"),
    FIFTY("50~59"),
    SIXTY("60~69"),
    SEVENTY("70~79"),
    EIGHTY("80~89"),
    NINETY("90~");

    public static AgeGroup of(String input) {
        return Arrays.stream(values())
            .filter(ageGroup -> ageGroup.isEqual(input))
            .findAny()
            .orElseThrow(() -> new ValidationException(TYPE_VALIDATED));
    }

    private boolean isEqual(String input) {
        return input.equals(this.description);
    }

    private final String description;
}
