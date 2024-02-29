package com.civilwar.boardsignal.review.presentation;

import com.civilwar.boardsignal.review.application.ReviewService;
import com.civilwar.boardsignal.review.dto.ApiReviewSaveRequest;
import com.civilwar.boardsignal.review.dto.request.ReviewSaveRequest;
import com.civilwar.boardsignal.review.dto.response.ReviewSaveResponse;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{roomId}")
    public ResponseEntity<ReviewSaveResponse> postReviews(
        @RequestBody ApiReviewSaveRequest apiReviewSaveRequest,
        @AuthenticationPrincipal User user,
        @PathVariable("roomId") Long roomId
    ) {
        List<ReviewSaveRequest> reviews = apiReviewSaveRequest.reviews();

        ReviewSaveResponse reviewSaveResponse = reviewService.postReview(reviews, user,
            roomId);

        return ResponseEntity.ok(reviewSaveResponse);
    }

}
