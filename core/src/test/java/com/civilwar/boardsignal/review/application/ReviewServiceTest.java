package com.civilwar.boardsignal.review.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.civilwar.boardsignal.review.domain.constant.ReviewContent;
import com.civilwar.boardsignal.review.domain.constant.ReviewRecommend;
import com.civilwar.boardsignal.review.domain.entity.Review;
import com.civilwar.boardsignal.review.domain.repository.ReviewRepository;
import com.civilwar.boardsignal.review.dto.request.ReviewEvaluationDto;
import com.civilwar.boardsignal.review.dto.request.ReviewSaveRequest;
import com.civilwar.boardsignal.review.dto.response.ReviewSaveResponse;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("[리뷰를 생성할 수 있다]")
    void postReviewTest() {
        //given
        Long roomId = 2L;
        User loginUser = UserFixture.getUserFixture("providerId", "testUrl");
        ReflectionTestUtils.setField(loginUser, "id", 100L);

        List<ReviewSaveRequest> reviewSaveRequests = new ArrayList<>();
        for (long i = 0; i < 3; i++) {
            //리뷰 평가 생성
            ReviewEvaluationDto reviewEvaluationDto1 = new ReviewEvaluationDto(
                ReviewContent.GOOD_MANNER.getDescription(),
                ReviewRecommend.LIKE.getMessage()
            );
            ReviewEvaluationDto reviewEvaluationDto2 = new ReviewEvaluationDto(
                ReviewContent.FAST_RESPONSE.getDescription(),
                ReviewRecommend.NON_REVIEW.getMessage()
            );
            ReviewEvaluationDto reviewEvaluationDto3 = new ReviewEvaluationDto(
                ReviewContent.TIME_COMMITMENT.getDescription(),
                ReviewRecommend.DISLIKE.getMessage()
            );

            //함께 참여한 유저들에 대한 리뷰
            ReviewSaveRequest request = new ReviewSaveRequest(i, List.of(
                reviewEvaluationDto1,
                reviewEvaluationDto2,
                reviewEvaluationDto3
            ));
            reviewSaveRequests.add(request);

            User reviewee = UserFixture.getUserFixture2("providerId", "testURL");
            ReflectionTestUtils.setField(reviewee, "id", i);

            given(userRepository.findById(i)).willReturn(Optional.of(reviewee));
        }
        given(reviewRepository.existsReviewByReviewerIdAndRoomId(loginUser.getId(),
            roomId)).willReturn(false);

        //when
        ReviewSaveResponse reviewSaveResponse = reviewService.postReview(reviewSaveRequests,
            loginUser, roomId);

        //then
        Assertions.assertThat(reviewSaveResponse.reviewIds()).hasSize(3);
        verify(reviewRepository, times(3)).save(any(Review.class));
    }
}