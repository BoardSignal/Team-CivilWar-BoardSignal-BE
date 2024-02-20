package com.civilwar.boardsignal.review.domain.repository;

import com.civilwar.boardsignal.review.domain.entity.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository {

    Review save(Review review);

    Optional<Review> findById(Long id);

    List<Review> findAll();

    List<Review> findReviewsByRevieweeId(Long userId);
}
