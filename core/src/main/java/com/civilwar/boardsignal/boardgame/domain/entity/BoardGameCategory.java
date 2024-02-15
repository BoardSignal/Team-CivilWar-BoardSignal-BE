package com.civilwar.boardsignal.boardgame.domain.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "BOARD_GAME_CATEGORY_TABLE")
public class BoardGameCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_GAME_CATEGORY_ID")
    private Long id;

    @JoinColumn(name = "board_game_id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private BoardGame boardGame;

    private Category category;

    @Builder(access = AccessLevel.PRIVATE)
    private BoardGameCategory(
        @NonNull Category category
    ) {
        this.category = category;
    }

    public static BoardGameCategory of(
        Category category
    ){
        return BoardGameCategory.builder()
            .category(category)
            .build();
    }

    public void connectBoardGame(BoardGame boardGame) {
        this.boardGame = boardGame;
    }
}
