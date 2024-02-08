package com.civilwar.boardsignal.boardgame.domain.entity;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

import com.civilwar.boardsignal.boardgame.domain.constant.Difficulty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "BOARD_GAME_TABLE")
public class BoardGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_GAME_ID")
    private Long id;

    private String title;

    private String description;

    private int minParticipants;

    private int maxParticipants;

    private int playTime;

    private Difficulty difficulty;

    private String youtubeUrl;

    private String mainImageUrl;

    private int wishCount;

    @OneToMany(mappedBy = "boardGame", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<BoardGameCategory> categories;
}
