package com.civilwar.boardsignal.chat.dto.response;

import java.util.List;

public record ChatPageResponse<T>(

    List<T> chatList,
    int page,
    int size,
    boolean hasNext
    ) {
}
