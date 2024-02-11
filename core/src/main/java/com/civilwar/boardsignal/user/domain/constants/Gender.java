package com.civilwar.boardsignal.user.domain.constants;

import static com.civilwar.boardsignal.common.exception.ValidationErrorCode.TYPE_VALIDATED;

import com.civilwar.boardsignal.common.exception.ValidationException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {

    MALE("male"),
    FEMALE("female");

    private final String type;

    public static Gender of(String input) {
        return Arrays.stream(values())
            .filter(gender -> gender.isEqual(input))
            .findAny()
            .orElseThrow(() -> new ValidationException(TYPE_VALIDATED));
    }

    private boolean isEqual(String input) {
        return input.equals(this.type);
    }
}
