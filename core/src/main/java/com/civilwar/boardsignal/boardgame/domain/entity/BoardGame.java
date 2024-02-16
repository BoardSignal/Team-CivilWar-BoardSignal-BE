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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "BOARD_GAME_TABLE")
public class BoardGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_GAME_ID")
    private Long id;

    @Column(name = "BOARD_GAME_TITLE")
    private String title;

    @Column(name = "BOARD_GAME_DESCRIPTION")
    private String description;

    @Column(name = "BOARD_GAME_MIN_PARTICIPANTS")
    private int minParticipants;

    @Column(name = "BOARD_GAME_MAX_PARTICIPANTS")
    private int maxParticipants;

    @Column(name = "BOARD_GAME_FROM_PLAY_TIME")
    private int fromPlayTime;

    @Column(name = "BOARD_GAME_TO_PLAY_TIME")
    private int toPlayTime;

    @Column(name = "BOARD_GAME_DIFFICULTY")
    private Difficulty difficulty;

    @Column(name = "BOARD_GAME_YOUTUBE_URL")
    private String youtubeUrl;

    @Column(name = "BOARD_GAME_MAIN_IMAGE_URL")
    private String mainImageUrl;

    @Column(name = "BOARD_GAME_WISH_COUNT")
    private int wishCount;

    @OneToMany(mappedBy = "boardGame", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<BoardGameCategory> categories;

    @Builder(access = AccessLevel.PRIVATE)
    private BoardGame(
        @NonNull String title,
        @NonNull String description,
        int minParticipants,
        int maxParticipants,
        int fromPlayTime,
        int toPlayTime,
        @NonNull Difficulty difficulty,
        @NonNull String youtubeUrl,
        @NonNull String mainImageUrl,
        @NonNull List<BoardGameCategory> categories) {
        this.title = title;
        this.description = description;
        this.minParticipants = minParticipants;
        this.maxParticipants = maxParticipants;
        this.fromPlayTime = fromPlayTime;
        this.toPlayTime = toPlayTime;
        this.difficulty = difficulty;
        this.youtubeUrl = youtubeUrl;
        this.mainImageUrl = mainImageUrl;
        this.wishCount = 0;
        this.categories = categories;
        categories.forEach(category -> category.connectBoardGame(this));
    }

    public static BoardGame of(
        String title,
        String description,
        int minParticipants,
        int maxParticipants,
        int fromPlayTime,
        int toPlayTime,
        Difficulty difficulty,
        String youtubeUrl,
        String mainImageUrl,
        List<BoardGameCategory> categories
    ) {
        return BoardGame.builder()
            .title(title)
            .description(description)
            .minParticipants(minParticipants)
            .maxParticipants(maxParticipants)
            .fromPlayTime(fromPlayTime)
            .toPlayTime(toPlayTime)
            .difficulty(difficulty)
            .youtubeUrl(youtubeUrl)
            .mainImageUrl(mainImageUrl)
            .categories(categories)
            .build();
    }
}
