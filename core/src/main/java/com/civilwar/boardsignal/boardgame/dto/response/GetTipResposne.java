package com.civilwar.boardsignal.boardgame.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record GetTipResposne(
    String nickname,
    String profileImageUrl,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime createdAt,
    String content
) {

}
