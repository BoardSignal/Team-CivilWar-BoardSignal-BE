package com.civilwar.boardsignal.review.dto.request;

import java.util.List;

public record ReviewSaveRequest(
    Long revieweeId,
    List<ReviewEvaluationDto> reviewContent
) {

}
