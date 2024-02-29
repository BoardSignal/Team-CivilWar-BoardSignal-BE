package com.civilwar.boardsignal.review.domain.entity;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.lang.NonNull;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "REVIEW_TABLE")
public class Review {

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "review", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    List<ReviewEvaluation> reviewEvaluations = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private Long id;

    @Column(name = "REVIEW_REVIEWER_ID")
    private Long reviewerId;

    @Column(name = "REVIEW_REVIEWEE_ID")
    private Long revieweeId;

    @Column(name = "REVIEW_ROOM_ID")
    private Long roomId;

    @Builder(access = AccessLevel.PRIVATE)
    private Review(
        @NonNull Long reviewerId,
        @NonNull Long revieweeId,
        @NonNull Long roomId,
        @NonNull List<ReviewEvaluation> reviewEvaluations
    ) {
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.roomId = roomId;
        this.reviewEvaluations.addAll(reviewEvaluations);
        reviewEvaluations
            .forEach(evaluation -> evaluation.associateReview(this));
    }

    public static Review of(
        Long reviewerId,
        Long revieweeId,
        Long roomId,
        List<ReviewEvaluation> reviewEvaluations
    ) {
        return Review.builder()
            .reviewerId(reviewerId)
            .revieweeId(revieweeId)
            .roomId(roomId)
            .reviewEvaluations(reviewEvaluations)
            .build();
    }
}
