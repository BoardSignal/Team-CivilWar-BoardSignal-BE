package com.civilwar.boardsignal.review.domain.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.EnumType.STRING;

import com.civilwar.boardsignal.common.base.BaseEntity;
import com.civilwar.boardsignal.review.domain.constant.ReviewContent;
import com.civilwar.boardsignal.review.domain.constant.ReviewRecommend;
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
import org.springframework.lang.NonNull;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "REVIEW_EVALUATION_TABLE")
public class ReviewEvaluation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_EVALUATION_ID")
    private Long id;

    @JoinColumn(name = "review_id", foreignKey = @ForeignKey(NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review;

    @Column(name = "REVIEW_EVALUATION_CONTENT")
    @Enumerated(STRING)
    private ReviewContent content;

    @Column(name = "REVIEW_IS_RECOMMENDED")
    private ReviewRecommend recommend;

    @Builder(access = AccessLevel.PRIVATE)
    public ReviewEvaluation(
        @NonNull ReviewContent content,
        @NonNull ReviewRecommend recommend
    ) {
        this.content = content;
        this.recommend = recommend;
    }

    public static ReviewEvaluation of(
        ReviewContent content,
        ReviewRecommend recommend
    ) {
        return ReviewEvaluation.builder()
            .content(content)
            .recommend(recommend)
            .build();
    }

    public void associateReview(Review review) {
        this.review = review;
    }
}
