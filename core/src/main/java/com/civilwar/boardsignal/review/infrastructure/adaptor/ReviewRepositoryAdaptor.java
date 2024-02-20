package com.civilwar.boardsignal.review.infrastructure.adaptor;

import com.civilwar.boardsignal.review.domain.entity.Review;
import com.civilwar.boardsignal.review.domain.repository.ReviewRepository;
import com.civilwar.boardsignal.review.infrastructure.repository.ReviewJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryAdaptor implements ReviewRepository {

    private final ReviewJpaRepository reviewJpaRepository;

    @Override
    public Review save(Review review) {
        return reviewJpaRepository.save(review);
    }

    @Override
    public Optional<Review> findById(Long id) {
        return reviewJpaRepository.findById(id);
    }

    @Override
    public List<Review> findAll() {
        return reviewJpaRepository.findAll();
    }

    @Override
    public List<Review> findReviewsByRevieweeId(Long userId) {
        return reviewJpaRepository.findReviewsByRevieweeId(userId);
    }
}
