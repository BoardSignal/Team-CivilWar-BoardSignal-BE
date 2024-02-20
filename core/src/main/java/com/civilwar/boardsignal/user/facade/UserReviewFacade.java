package com.civilwar.boardsignal.user.facade;

import com.civilwar.boardsignal.review.domain.constant.ReviewContent;
import com.civilwar.boardsignal.review.domain.entity.Review;
import com.civilwar.boardsignal.review.domain.entity.ReviewEvaluation;
import com.civilwar.boardsignal.review.domain.repository.ReviewRepository;
import com.civilwar.boardsignal.user.dto.response.UserReviewResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserReviewFacade {

    private final ReviewRepository reviewRepository;

    public List<UserReviewResponse> getUserReview(Long userId) {

        //1. 유저에 대한 리뷰 조회
        List<Review> reviews = reviewRepository.findReviewsByRevieweeId(userId);

        //2. 리뷰 평가 모두 한 곳에 저장
        List<ReviewEvaluation> userEvaluations = new ArrayList<>();
        for (Review review : reviews) {
            userEvaluations.addAll(review.getReviewEvaluations());
        }

        //리뷰 조회 결과를 저장할 리스트
        List<UserReviewResponse> result = new ArrayList<>();

        //3. 리뷰 항목 만큼 반복문
        ReviewContent[] allContents = ReviewContent.values();
        for (ReviewContent content : allContents) {

            //4. 유저에 대한 각 항목의 좋아요 합계 계산
            int totalScore = userEvaluations.stream()
                .filter(evaluation -> evaluation.getContent().equals(content))
                .mapToInt(ReviewEvaluation::getIsRecommended)
                .filter(isRecommended -> isRecommended == 1)
                .sum();

            //5. 정리된 리뷰 & 데이터 저장
            result.add(new UserReviewResponse(content.getDescription(), totalScore));
        }

        return result;
    }

}
