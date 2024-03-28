package com.civilwar.boardsignal.boardgame.domain.entity;

import com.civilwar.boardsignal.common.base.BaseEntity;
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
@Table(name = "LIKE_TABLE")
public class Like extends BaseEntity {

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
        @NonNull Long tipId,
        @NonNull Long userId
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
