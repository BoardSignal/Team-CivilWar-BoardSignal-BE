package com.civilwar.boardsignal.review.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.civilwar.boardsignal.common.support.ApiTestSupport;
import com.civilwar.boardsignal.review.domain.constant.ReviewContent;
import com.civilwar.boardsignal.review.domain.constant.ReviewRecommend;
import com.civilwar.boardsignal.review.domain.entity.Review;
import com.civilwar.boardsignal.review.domain.entity.ReviewEvaluation;
import com.civilwar.boardsignal.review.domain.repository.ReviewRepository;
import com.civilwar.boardsignal.review.dto.ApiReviewSaveRequest;
import com.civilwar.boardsignal.review.dto.request.ReviewEvaluationDto;
import com.civilwar.boardsignal.review.dto.request.ReviewSaveRequest;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

class ReviewControllerTest extends ApiTestSupport {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Test
    @DisplayName("[사용자는 같이 게임한 여러 유저에 대해 한번에 리뷰한다.]")
    void postReviews() throws Exception {
        //given
        User user1 = UserFixture.getUserFixture("providerId", "URL");
        userRepository.save(user1);
        User user2 = UserFixture.getUserFixture("providerId", "URL");
        userRepository.save(user2);
        User user3 = UserFixture.getUserFixture("providerId", "URL");
        userRepository.save(user3);

        Long roomId = 2L;
        List<ReviewSaveRequest> reviewSaveRequests = new ArrayList<>();
        for (long i = 2; i < 5; i++) {
            //리뷰 평가 생성
            ReviewEvaluationDto reviewEvaluationDto1 = new ReviewEvaluationDto(
                ReviewContent.GOOD_MANNER.getDescription(),
                ReviewRecommend.LIKE.getMessage()
            );
            ReviewEvaluationDto reviewEvaluationDto2 = new ReviewEvaluationDto(
                ReviewContent.FAST_RESPONSE.getDescription(),
                ReviewRecommend.LIKE.getMessage()
            );
            ReviewEvaluationDto reviewEvaluationDto3 = new ReviewEvaluationDto(
                ReviewContent.TIME_COMMITMENT.getDescription(),
                ReviewRecommend.LIKE.getMessage()
            );

            //함께 참여한 유저들에 대한 리뷰
            ReviewSaveRequest request = new ReviewSaveRequest(i, List.of(
                reviewEvaluationDto1,
                reviewEvaluationDto2,
                reviewEvaluationDto3
            ));
            reviewSaveRequests.add(request);
        }
        ApiReviewSaveRequest apiReviewSaveRequest = new ApiReviewSaveRequest(reviewSaveRequests);

        //when

        //then
        mockMvc.perform(post("/api/v1/reviews/" + roomId)
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(apiReviewSaveRequest)))
            .andExpect(jsonPath("$.reviewIds.length()").value(3));

        Review review1 = reviewRepository.findById(1L).orElseThrow();
        List<ReviewEvaluation> reviewEvaluations = review1.getReviewEvaluations();
        ReviewEvaluation reviewEvaluation1 = reviewEvaluations.get(0);
        ReviewEvaluation reviewEvaluation2 = reviewEvaluations.get(1);
        ReviewEvaluation reviewEvaluation3 = reviewEvaluations.get(2);

        assertThat(review1.getReviewerId()).isEqualTo(loginUser.getId());
        assertThat(review1.getRevieweeId()).isEqualTo(user1.getId());

        assertThat(reviewEvaluation1.getContent()).isEqualTo(ReviewContent.GOOD_MANNER);
        assertThat(reviewEvaluation1.getRecommend()).isEqualTo(ReviewRecommend.LIKE);
        assertThat(reviewEvaluation2.getContent()).isEqualTo(ReviewContent.FAST_RESPONSE);
        assertThat(reviewEvaluation2.getRecommend()).isEqualTo(ReviewRecommend.LIKE);
        assertThat(reviewEvaluation3.getContent()).isEqualTo(ReviewContent.TIME_COMMITMENT);
        assertThat(reviewEvaluation3.getRecommend()).isEqualTo(ReviewRecommend.LIKE);

        assertThat(user1.getMannerScore()).isEqualTo(37.5);
        assertThat(user2.getMannerScore()).isEqualTo(37.5);
        assertThat(user3.getMannerScore()).isEqualTo(37.5);
    }
}