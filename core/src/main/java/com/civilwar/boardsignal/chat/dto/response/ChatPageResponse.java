package com.civilwar.boardsignal.chat.dto.response;

import java.util.List;

public record ChatPageResponse<T>(

    List<T> chatList,
    int currentPageNumber,
    int size,
    boolean hasNext
    ) {
}
