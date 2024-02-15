package com.civilwar.boardsignal.boardgame.dto.response;


import java.util.List;

public record BoardGamePageResponse<T>(
    List<T> boardGamesInfos,
    int size,
    int currentPage

) {

}
