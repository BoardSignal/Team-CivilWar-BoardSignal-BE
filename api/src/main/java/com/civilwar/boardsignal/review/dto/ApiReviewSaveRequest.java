package com.civilwar.boardsignal.review.dto;

import com.civilwar.boardsignal.review.dto.request.ReviewSaveRequest;
import java.util.List;

public record ApiReviewSaveRequest(
    List<ReviewSaveRequest> reviews
) {

}
