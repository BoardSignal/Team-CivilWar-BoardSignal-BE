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
@Table(name = "LIKE_TABLE")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LIKE_ID")
    private Long id;

    @Column(name = "LIKE_TIP_ID")
    private Long tipId;

    @Column(name = "LIKE_USER_ID")
    private Long userId;

    @Builder(access = AccessLevel.PRIVATE)
    private Like(
        Long tipId,
        Long userId
    ) {
        this.tipId = tipId;
        this.userId = userId;
    }

    public static Like of(
        Long tipId,
        Long userId
    ) {
        return Like.builder()
            .tipId(tipId)
            .userId(userId)
            .build();
    }

}
