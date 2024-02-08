package com.civilwar.boardsignal.user.domain.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeGroup {

    UNDER_FOURTEEN("14세 미만"),
    TEENAGER("10대"),
    TWENTY("20대"),
    THIRTY("30대"),
    FORTY("40대"),
    FIFTY("50대"),
    SIXTY("60대"),
    SEVENTY("70대"),
    EIGHTY("80대"),
    NINETY("90대");

    private final String description;
}
