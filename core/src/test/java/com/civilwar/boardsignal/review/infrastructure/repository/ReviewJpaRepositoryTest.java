package com.civilwar.boardsignal.review.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.civilwar.boardsignal.common.support.DataJpaTestSupport;
import com.civilwar.boardsignal.review.ReviewFixture;
import com.civilwar.boardsignal.review.domain.entity.Review;
import com.civilwar.boardsignal.review.domain.entity.ReviewEvaluation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
class ReviewJpaRepositoryTest extends DataJpaTestSupport {

    Long reviewerId = 1L;
    Long revieweeId = 100L;
    Long roomId = 1L;
    @Autowired
    private ReviewJpaRepository reviewJpaRepository;

    @Test
    @DisplayName("[사용자가 받은 리뷰 별 평가를 갖고온다 (쿼리 확인용)]")
    void findReviewsByRevieweeIdTest() {

        //given
        List<ReviewEvaluation> evaluations = ReviewFixture.getEvaluationFixture();
        Review reviewFixture = ReviewFixture.getReviewFixture(reviewerId, revieweeId, roomId,
            evaluations);
        reviewJpaRepository.save(reviewFixture);

        //when
        List<Review> reviews = reviewJpaRepository.findReviewsByRevieweeId(revieweeId);

        //then
        assertThat(reviews.get(0)).isNotNull();
        assertThat(reviews.get(0).getReviewEvaluations()).hasSize(3);

    }
}