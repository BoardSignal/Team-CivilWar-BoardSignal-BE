package com.civilwar.boardsignal.review.dto.response;


import java.util.List;

public record ReviewSaveResponse(
    List<Long> reviewIds
) {

}
