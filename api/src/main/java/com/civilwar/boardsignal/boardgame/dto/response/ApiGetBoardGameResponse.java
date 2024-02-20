package com.civilwar.boardsignal.boardgame.dto.response;

import java.util.List;

public record ApiGetBoardGameResponse(
    Long boardGameId,
    String name,
    String description,
    List<String> categories,
    String difficulty,
    int minParticipants,
    int maxParticipants,
    int fromPlayTime,
    int toPlayTime,
    int wishCount,
    String imageUrl,
    List<ApiGetTipResponse> tips
) {

}
