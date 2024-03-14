package com.civilwar.boardsignal.review.facade;

import static org.assertj.core.api.Assertions.assertThat;

import com.civilwar.boardsignal.notification.dto.request.NotificationRequest;
import com.civilwar.boardsignal.review.domain.constant.ReviewContent;
import com.civilwar.boardsignal.review.domain.constant.ReviewRecommend;
import com.civilwar.boardsignal.review.dto.request.ReviewEvaluationDto;
import com.civilwar.boardsignal.review.dto.request.ReviewSaveRequest;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@RecordApplicationEvents
class ReviewFacadeTest {

    @Autowired
    private ReviewFacade reviewFacade;

    @Autowired
    private ApplicationEvents events;

    @Test
    @DisplayName("[리뷰를 받은 유저들에 대한 알림 이벤트가 동작한다.]")
    @Disabled
    void postReviewNotification() {
        //given
        Long roomId = 2L;
        User loginUser = UserFixture.getUserFixture("providerId", "testUrl");
        ReflectionTestUtils.setField(loginUser, "id", 3L);

        int reviewCount = 3;

        List<ReviewSaveRequest> reviewSaveRequests = new ArrayList<>();
        for (long i = 1; i <= reviewCount; i++) {
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
        }

        //when
        reviewFacade.postReview(reviewSaveRequests, loginUser, roomId);

        //이벤트 호출 횟수
        int count = (int) events.stream(NotificationRequest.class).count();

        //then
        assertThat(count).isEqualTo(1);
    }

}