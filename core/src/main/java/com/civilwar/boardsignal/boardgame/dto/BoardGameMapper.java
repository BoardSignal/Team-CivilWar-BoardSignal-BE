package com.civilwar.boardsignal.boardgame.dto;

import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.entity.Tip;
import com.civilwar.boardsignal.boardgame.dto.response.BoardGamePageResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetAllBoardGamesResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetBoardGameResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetTipResposne;
import com.civilwar.boardsignal.boardgame.dto.response.MyTipResponse;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BoardGameMapper {

    public static GetAllBoardGamesResponse toGetAllBoardGamesResponse(BoardGame boardGame) {
        List<String> boardGameCategories = boardGame.getCategories().stream()
            .map(category -> category.getCategory().getDescription())
            .toList();
        String boardGameDifficulty = boardGame.getDifficulty().getDescription();

        return new GetAllBoardGamesResponse(
            boardGame.getId(),
            boardGame.getTitle(),
            boardGameCategories,
            boardGameDifficulty,
            boardGame.getMinParticipants(),
            boardGame.getMaxParticipants(),
            boardGame.getFromPlayTime(),
            boardGame.getToPlayTime(),
            boardGame.getWishCount(),
            boardGame.getMainImageUrl()
        );
    }

    public static <T> BoardGamePageResponse<T> toBoardGamePageRepsonse(Slice<T> page) {
        return new BoardGamePageResponse<>(
            page.getContent(),
            page.getSize(),
            page.hasNext()
        );
    }

    public static GetBoardGameResponse toGetBoardGameResponse(
        BoardGame boardGame,
        MyTipResponse myTipResponse,
        List<GetTipResposne> tips
    ) {
        List<String> categories = boardGame.getCategories().stream()
            .map(category -> category.getCategory().getDescription())
            .toList();

        return new GetBoardGameResponse(
            boardGame.getId(),
            boardGame.getTitle(),
            boardGame.getDescription(),
            categories,
            boardGame.getDifficulty().getDescription(),
            boardGame.getMinParticipants(),
            boardGame.getMaxParticipants(),
            boardGame.getFromPlayTime(),
            boardGame.getToPlayTime(),
            boardGame.getWishCount(),
            boardGame.getMainImageUrl(),
            myTipResponse,
            tips
        );
    }

    public static GetTipResposne toGetTipResponse(User user, Tip tip) {
        return new GetTipResposne(
            tip.getId(),
            user.getNickname(),
            user.getProfileImageUrl(),
            tip.getCreatedAt(),
            tip.getContent(),
            tip.getLikeCount()
        );
    }

    public static MyTipResponse toMyTip(User user, Tip tip) {
        return new MyTipResponse(
            tip.getId(),
            user.getNickname(),
            user.getProfileImageUrl(),
            tip.getCreatedAt(),
            tip.getContent(),
            tip.getLikeCount()
        );
    }

}
