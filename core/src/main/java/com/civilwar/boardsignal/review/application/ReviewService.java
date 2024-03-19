package com.civilwar.boardsignal.review.application;

import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.common.exception.ValidationException;
import com.civilwar.boardsignal.review.domain.constant.ReviewContent;
import com.civilwar.boardsignal.review.domain.constant.ReviewRecommend;
import com.civilwar.boardsignal.review.domain.entity.Review;
import com.civilwar.boardsignal.review.domain.entity.ReviewEvaluation;
import com.civilwar.boardsignal.review.domain.repository.ReviewRepository;
import com.civilwar.boardsignal.review.dto.request.ReviewEvaluationDto;
import com.civilwar.boardsignal.review.dto.request.ReviewSaveRequest;
import com.civilwar.boardsignal.review.dto.response.ReviewSaveResponse;
import com.civilwar.boardsignal.review.exception.ReviewErrorCode;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import com.civilwar.boardsignal.user.exception.UserErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewSaveResponse postReview(
        List<ReviewSaveRequest> reviewRequests,
        User loginUser,
        Long roomId
    ) {

        if(reviewRepository.existsReviewByReviewerIdAndRoomId(loginUser.getId(), roomId)) {
            throw new ValidationException(ReviewErrorCode.ALREADY_EXIST_REVIEW);
        }

        //유저가 남긴 리뷰 id 저장
        List<Long> reviewIds = new ArrayList<>();

        //유저들에 대한 리뷰들
        for (ReviewSaveRequest reviewRequest : reviewRequests) {
            //리뷰를 받은 유저 id
            Long revieweeId = reviewRequest.revieweeId();

            List<ReviewEvaluation> reviewEvaluations = new ArrayList<>();

            //유저에 대한 평가들
            List<ReviewEvaluationDto> reviewEvaluationDtos = reviewRequest.reviewContent();
            for (ReviewEvaluationDto reviewEvaluationDto : reviewEvaluationDtos) {
                //리뷰 평가 추출
                ReviewEvaluation reviewEvaluation = ReviewEvaluation.of(
                    ReviewContent.of(reviewEvaluationDto.content()),
                    ReviewRecommend.of(reviewEvaluationDto.reviewScore())
                );
                //리뷰 평가 임시 저장
                reviewEvaluations.add(reviewEvaluation);
            }

            //유저에 대한 리뷰 생성
            Review review = Review.of(loginUser.getId(), revieweeId, roomId, reviewEvaluations);
            reviewRepository.save(review);

            User reviewee = userRepository.findById(revieweeId)
                .orElseThrow(() -> new NotFoundException(UserErrorCode.NOT_FOUND_USER));

            double sumScore = reviewEvaluations.stream()
                .mapToDouble(reviewEvaluation -> reviewEvaluation.getRecommend().getScore())
                .sum();
            double averageScore = sumScore / reviewEvaluationDtos.size();

            reviewee.updateMannerScore(averageScore);

            reviewIds.add(review.getId());
        }

        return new ReviewSaveResponse(reviewIds);
    }

}
