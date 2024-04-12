package com.civilwar.boardsignal.review.infrastructure.repository;

import com.civilwar.boardsignal.review.domain.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review as r "
        + "where r.revieweeId = :userId")
    List<Review> findReviewsByRevieweeId(@Param("userId") Long userId);

    @Query("select r from Review as r "
        + "where r.reviewerId = :reviewerId "
        + "and r.roomId in :roomIds ")
    List<Review> findReviewsByRoomIdsAndReviewer(@Param("roomIds") List<Long> roomIds,
        @Param("reviewerId") Long reviewerId);

    boolean existsReviewByReviewerIdAndRoomId(Long reviewerId, Long roomId);
}
