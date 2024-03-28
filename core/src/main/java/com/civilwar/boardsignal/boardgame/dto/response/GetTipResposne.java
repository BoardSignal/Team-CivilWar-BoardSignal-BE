package com.civilwar.boardsignal.boardgame.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record GetTipResposne(
    Long tipId,
    String nickname,
    String profileImageUrl,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    LocalDateTime createdAt,
    String content,
    int likeCount,
    boolean isLiked
) {

}
