package com.civilwar.boardsignal.boardgame.dto.mapper;

import com.civilwar.boardsignal.boardgame.dto.request.AddTipRequest;
import com.civilwar.boardsignal.boardgame.dto.request.ApiAddTipRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardGameApiMapper {

    public static AddTipRequest toAddTipRequest(ApiAddTipRequest request) {
        return new AddTipRequest(request.content());
    }
}
