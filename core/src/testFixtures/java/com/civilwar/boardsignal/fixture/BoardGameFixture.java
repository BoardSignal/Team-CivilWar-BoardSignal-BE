package com.civilwar.boardsignal.fixture;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.boardgame.domain.constant.Difficulty;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGameCategory;
import com.civilwar.boardsignal.boardgame.domain.entity.Tip;
import com.civilwar.boardsignal.boardgame.domain.entity.Wish;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BoardGameFixture {

    public static BoardGame getBoardGame(List<BoardGameCategory> categories) {
        return BoardGame.of(
            "달무티",
            "재밌는 게임입니다.",
            3,
            5,
            20,
            40,
            Difficulty.NORMAL,
            "http~youtube",
            "http~image",
            categories
        );
    }

    public static BoardGame getBoardGame2(List<BoardGameCategory> categories) {
        return BoardGame.of(
            "스컬킹",
            "흥미로운 게임입니다.",
            2,
            4,
            50,
            80,
            Difficulty.HARD,
            "http~youtube",
            "http~image",
            categories
        );
    }

    public static BoardGameCategory getBoardGameCategory(Category category) {
        return BoardGameCategory.of(category);
    }

    public static Wish getWish(Long userId, Long boardGameId) {
        return Wish.of(userId, boardGameId);
    }

    public static Tip getTip(Long userId, Long boardGameId, String content) {
        return Tip.of(boardGameId, userId, content);
    }
}
