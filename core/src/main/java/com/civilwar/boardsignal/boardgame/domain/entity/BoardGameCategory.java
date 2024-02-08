package com.civilwar.boardsignal.boardgame.domain.entity;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "BOARD_GAME_CATEGORY_TABLE")
public class BoardGameCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_GAME_CATEGORY_ID")
    private Long id;

    @JoinColumn(name = "board_game_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private BoardGame boardGame;

    private Category category;
}
