package com.civilwar.boardsignal.boardgame.dto;

import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.dto.response.BoardGamePageResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetAllBoardGamesResponse;
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

}
