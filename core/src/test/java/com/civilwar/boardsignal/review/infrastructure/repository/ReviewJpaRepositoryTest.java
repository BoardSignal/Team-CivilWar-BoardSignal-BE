package com.civilwar.boardsignal.review.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.civilwar.boardsignal.common.support.DataJpaTestSupport;
import com.civilwar.boardsignal.review.ReviewFixture;
import com.civilwar.boardsignal.review.domain.entity.Review;
import com.civilwar.boardsignal.review.domain.entity.ReviewEvaluation;
import com.civilwar.boardsignal.room.RoomFixture;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.repository.ParticipantRepository;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
class ReviewJpaRepositoryTest extends DataJpaTestSupport {

    Long reviewerId = 1L;
    Long revieweeId = 100L;
    Long roomId = 1L;

    @Autowired
    private ReviewJpaRepository reviewJpaRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParticipantRepository participantRepository;

    @Test
    @DisplayName("[사용자가 받은 리뷰 별 평가를 갖고온다]")
    void findReviewsByRevieweeIdTest() {

        //given
        List<ReviewEvaluation> evaluations = ReviewFixture.getEvaluationFixture();
        Review reviewFixture = ReviewFixture.getReviewFixture(reviewerId, revieweeId, roomId,
            evaluations);
        reviewJpaRepository.save(reviewFixture);

        //when
        List<Review> reviews = reviewJpaRepository.findReviewsByRevieweeId(revieweeId);

        //then
        assertThat(reviews.get(0)).isNotNull();
        assertThat(reviews.get(0).getReviewEvaluations()).hasSize(3);

    }

    @Test
    @DisplayName("[자신이 다른 방에 남긴 각각의 리뷰 2개를 갖고온다]")
    void findReviewsByRoomIdsAndReviewerTest() throws IOException {
        //given
        Room room = RoomFixture.getRoom(Gender.UNION);
        Room savedRoom = roomRepository.save(room);
        Room room2 = RoomFixture.getRoom(Gender.UNION);
        Room savedRoom2 = roomRepository.save(room2);

        User savedLeaderUser = userRepository.save(
            UserFixture.getUserFixture("providerId", "testURL"));
        User savedLeaderUser2 = userRepository.save(
            UserFixture.getUserFixture("providerId", "testURL"));
        User savedReviewer = userRepository.save(
            UserFixture.getUserFixture2("providerId", "testURL"));

        participantRepository.save(
            Participant.of(savedLeaderUser.getId(), savedRoom.getId(), true));
        participantRepository.save(
            Participant.of(savedReviewer.getId(), savedRoom2.getId(), false));

        List<ReviewEvaluation> reviewEvaluations = ReviewFixture.getEvaluationFixture();
        Review review = ReviewFixture.getReviewFixture(savedReviewer.getId(),
            savedLeaderUser.getId(), savedRoom.getId(), reviewEvaluations);
        reviewJpaRepository.save(review);

        Review review2 = ReviewFixture.getReviewFixture(savedReviewer.getId(),
            savedLeaderUser2.getId(), savedRoom2.getId(), reviewEvaluations);
        reviewJpaRepository.save(review2);

        //when
        List<Review> reviewsByRoomIdsAndReviewer = reviewJpaRepository
            .findReviewsByRoomIdsAndReviewer(List.of(savedRoom.getId(), savedRoom2.getId()),
                savedReviewer.getId());

        //then
        assertThat(reviewsByRoomIdsAndReviewer).hasSize(2);
    }
}