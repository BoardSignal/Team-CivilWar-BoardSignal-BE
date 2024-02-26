package com.civilwar.boardsignal.review;

import static lombok.AccessLevel.PRIVATE;

import com.civilwar.boardsignal.review.domain.constant.ReviewContent;
import com.civilwar.boardsignal.review.domain.constant.ReviewRecommend;
import com.civilwar.boardsignal.review.domain.entity.Review;
import com.civilwar.boardsignal.review.domain.entity.ReviewEvaluation;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class ReviewFixture {

    public static Review getReviewFixture(
        Long reviewerId,
        Long revieweeId,
        Long roomId,
        List<ReviewEvaluation> evaluations
    ) {
        return Review.of(
            reviewerId,
            revieweeId,
            roomId,
            evaluations
        );
    }

    public static List<ReviewEvaluation> getEvaluationFixture() {
        return List.of(
            ReviewEvaluation.of(
                ReviewContent.TIME_COMMITMENT,
                ReviewRecommend.LIKE
            ),
            ReviewEvaluation.of(
                ReviewContent.GOOD_MANNER,
                ReviewRecommend.NON_REVIEW
            ),
            ReviewEvaluation.of(
                ReviewContent.FAST_RESPONSE,
                ReviewRecommend.DISLIKE
            )
        );
    }

}
