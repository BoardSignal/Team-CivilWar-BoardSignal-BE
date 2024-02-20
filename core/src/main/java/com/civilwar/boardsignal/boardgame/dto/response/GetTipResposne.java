package com.civilwar.boardsignal.boardgame.dto.response;

import java.time.LocalDateTime;

public record GetTipResposne(
    String nickname,
    String profileImageUrl,
    LocalDateTime createdAt,
    String content
) {

}
