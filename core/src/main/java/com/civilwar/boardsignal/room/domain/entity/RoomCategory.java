package com.civilwar.boardsignal.room.domain.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.EnumType.STRING;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
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

@Entity
@NoArgsConstructor
@Getter
@Table(name = "ROOM_CATEGORY_TABLE")
public class RoomCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_CATEGORY_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_CATEGORY_ROOM_ID", foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Room room;

    @Column(name = "ROOM_CATEGORY_CATEGORY")
    @Enumerated(STRING)
    private Category category;

    @Builder(access = AccessLevel.PRIVATE)
    private RoomCategory(
        Room room,
        Category category
    ) {
        this.room = room;
        this.category = category;
    }

    public static RoomCategory of(
        Room room,
        Category category
    ) {
        return RoomCategory.builder()
            .room(room)
            .category(category)
            .build();
    }
}
