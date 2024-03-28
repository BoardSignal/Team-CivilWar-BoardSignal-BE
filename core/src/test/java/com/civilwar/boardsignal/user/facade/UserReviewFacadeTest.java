package com.civilwar.boardsignal.user.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.civilwar.boardsignal.review.ReviewFixture;
import com.civilwar.boardsignal.review.domain.entity.Review;
import com.civilwar.boardsignal.review.domain.entity.ReviewEvaluation;
import com.civilwar.boardsignal.review.domain.repository.ReviewRepository;
import com.civilwar.boardsignal.user.dto.response.UserReviewResponse;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserReviewFacadeTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private UserReviewFacade userReviewFacade;

    @Test
    @DisplayName("[유저가 평가 받은 리뷰를 항목 별로 정리한 데이터로 반환한다.]")
    void getUserReviewTest() {
        //given
        Long revieweeId = 100L;
        Long roomId = 1L;
        List<ReviewEvaluation> evaluations = ReviewFixture.getEvaluationFixture();
        List<Review> reviews = new ArrayList<>();
        for (long i = 0; i < 3; i++) {
            Review reviewFixture = ReviewFixture.getReviewFixture(i, revieweeId, roomId,
                evaluations);
            reviews.add(reviewFixture);
        }
        given(reviewRepository.findReviewsByRevieweeId(revieweeId)).willReturn(reviews);

        //when
        List<UserReviewResponse> userReviews = userReviewFacade.getUserReview(revieweeId);

        UserReviewResponse timeCommitmentReview = userReviews.get(0);
        UserReviewResponse goodMannerReview = userReviews.get(1);
        UserReviewResponse fastResponseReview = userReviews.get(2);

        //then
        assertThat(userReviews).hasSize(3);
        assertThat(timeCommitmentReview.score()).isEqualTo(3);
        assertThat(goodMannerReview.score()).isZero();
        assertThat(fastResponseReview.score()).isZero();
    }
}