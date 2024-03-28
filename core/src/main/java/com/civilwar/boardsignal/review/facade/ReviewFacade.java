package com.civilwar.boardsignal.review.facade;

import com.civilwar.boardsignal.notification.domain.constant.NotificationContent;
import com.civilwar.boardsignal.notification.dto.request.NotificationRequest;
import com.civilwar.boardsignal.review.application.ReviewService;
import com.civilwar.boardsignal.review.dto.request.ReviewSaveRequest;
import com.civilwar.boardsignal.review.dto.response.ReviewSaveResponse;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReviewFacade {

    private final ReviewService reviewService;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public ReviewSaveResponse postReview(
        List<ReviewSaveRequest> reviewRequests,
        User loginUser,
        Long roomId
    ) {
        ReviewSaveResponse postReviewResponse = reviewService.postReview(
            reviewRequests,
            loginUser,
            roomId
        );

        List<Long> revieweeIds = reviewRequests.stream()
            .map(ReviewSaveRequest::revieweeId)
            .toList();

        NotificationRequest notificationRequest = new NotificationRequest(
            NotificationContent.REVIEW_RECIEVED.getTitle(),
            NotificationContent.REVIEW_RECIEVED.getMessage(loginUser.getNickname()),
            loginUser.getProfileImageUrl(),
            null,
            revieweeIds
        );

        publisher.publishEvent(notificationRequest);

        return postReviewResponse;
    }

}
