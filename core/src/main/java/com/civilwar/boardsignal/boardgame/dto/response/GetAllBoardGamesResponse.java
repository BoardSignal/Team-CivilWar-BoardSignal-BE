package com.civilwar.boardsignal.boardgame.dto.response;

import java.util.List;

public record GetAllBoardGamesResponse(
    Long boardGameId,
    String name,
    List<String> categories,
    String difficulty,
    int minParticipants,
    int maxParticipants,
    int fromPlayTime,
    int toPlayTime,
    int wishCount,
    String imageUrl
) {

}
