package com.civilwar.boardsignal.boardgame.dto.response;

import java.util.List;

public record GetBoardGameResponse(
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
    boolean isWished,
    MyTipResponse myTip,
    List<GetTipResposne> tips
) {

}
