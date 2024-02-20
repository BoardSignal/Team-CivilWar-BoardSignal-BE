package com.civilwar.boardsignal.boardgame.dto.mapper;

import com.civilwar.boardsignal.boardgame.dto.request.AddTipRequest;
import com.civilwar.boardsignal.boardgame.dto.request.ApiAddTipRequest;
import com.civilwar.boardsignal.boardgame.dto.response.ApiGetBoardGameResponse;
import com.civilwar.boardsignal.boardgame.dto.response.ApiGetTipResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetBoardGameResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetTipResposne;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BoardGameApiMapper {

    public static AddTipRequest toAddTipRequest(ApiAddTipRequest request) {
        return new AddTipRequest(request.content());
    }

    public static ApiGetBoardGameResponse toApiGetBoardGameResponse(
        GetBoardGameResponse boardGame
    ) {
        List<ApiGetTipResponse> tips = boardGame.tips().stream()
            .map(BoardGameApiMapper::toApiGetTipResponse)
            .toList();

        return new ApiGetBoardGameResponse(
            boardGame.boardGameId(),
            boardGame.name(),
            boardGame.description(),
            boardGame.categories(),
            boardGame.difficulty(),
            boardGame.minParticipants(),
            boardGame.maxParticipants(),
            boardGame.fromPlayTime(),
            boardGame.toPlayTime(),
            boardGame.wishCount(),
            boardGame.imageUrl(),
            tips
        );
    }

    private static ApiGetTipResponse toApiGetTipResponse(GetTipResposne tip) {
        return new ApiGetTipResponse(
            tip.nickname(),
            tip.profileImageUrl(),
            tip.createdAt(),
            tip.content()
        );
    }
}
