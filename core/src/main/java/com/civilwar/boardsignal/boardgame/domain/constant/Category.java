package com.civilwar.boardsignal.boardgame.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {

    WAR("워게임"),
    FAMILY("가족게임"),
    STRATEGY("전략게임"),
    ABSTRACT_STRATEGY("추상게임"),
    THEMATIC("테마게임"),
    PARTY("파티게임"),
    CHILDREN("어린이게임"),
    CUSTOMIZABLE("컬렉터블게임");

    private final String description;
}
