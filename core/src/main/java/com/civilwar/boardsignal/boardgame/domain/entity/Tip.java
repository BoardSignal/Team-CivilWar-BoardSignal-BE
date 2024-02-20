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
import org.springframework.lang.NonNull;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "TIP_TABLE")
public class Tip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TIP_ID")
    private Long id;

    private Long boardGameId;

    private Long userId;

    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private Tip(
        @NonNull Long boardGameId,
        @NonNull Long userId,
        @NonNull String content
    ) {
        this.boardGameId = boardGameId;
        this.userId = userId;
        this.content = content;
    }

    public static Tip of(
        Long boardGameId,
        Long userId,
        String content
    ) {
        return Tip.builder()
            .boardGameId(boardGameId)
            .userId(userId)
            .content(content)
            .build();
    }
}
