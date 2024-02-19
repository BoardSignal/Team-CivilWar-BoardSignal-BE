package com.civilwar.boardsignal.boardgame.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "WISH_TABLE")
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WISH_ID")
    private Long id;

    private Long userId;

    private Long boardGameId;

    @Builder(access = AccessLevel.PRIVATE)
    private Wish(
        Long userId,
        Long boardGameId
    ) {
        this.userId = userId;
        this.boardGameId = boardGameId;
    }

    public static Wish of(
        Long userId,
        Long boardGameId
    ) {
        return Wish.builder()
            .userId(userId)
            .boardGameId(boardGameId)
            .build();
    }
}
