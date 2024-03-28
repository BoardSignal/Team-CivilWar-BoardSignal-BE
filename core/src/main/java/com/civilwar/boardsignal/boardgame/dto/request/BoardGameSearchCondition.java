package com.civilwar.boardsignal.boardgame.dto.request;

import java.util.List;

public record BoardGameSearchCondition(
    String difficulty,
    List<String> categories,
    Integer playTime,
    String searchKeyword
) {

}
