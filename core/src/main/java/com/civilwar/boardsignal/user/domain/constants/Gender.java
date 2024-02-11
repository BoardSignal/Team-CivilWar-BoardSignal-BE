package com.civilwar.boardsignal.user.domain.constants;

import static com.civilwar.boardsignal.user.exception.UserErrorCode.NOT_FOUND_GENDER;

import com.civilwar.boardsignal.common.exception.NotFoundException;
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
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_GENDER));
    }

    private boolean isEqual(String input) {
        return input.equalsIgnoreCase(this.type);
    }
}
