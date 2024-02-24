package com.civilwar.boardsignal.room.domain.repository;

import static org.assertj.core.api.Assertions.*;

import com.civilwar.boardsignal.common.support.DataJpaTestSupport;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.dto.response.ParticipantJpaDto;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ParticipantRepositoryTest extends DataJpaTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Test
    @DisplayName("[DB에서 Dto 프로젝션을 통해 참가자 정보를 가져온다]")
    void findParticipantByRoomIdTest() {
        //given
        Long roomId = 1L;
        User user1 = UserFixture.getUserFixture("providerId", "imageUrl");
        User user2 = UserFixture.getUserFixture2("providerId", "imageUrl");
        userRepository.save(user1);
        userRepository.save(user2);

        Participant participant1 = Participant.of(user1.getId(), roomId, true);
        Participant participant2 = Participant.of(user2.getId(), roomId, false);
        participantRepository.save(participant1);
        participantRepository.save(participant2);

        //when
        List<ParticipantJpaDto> participants = participantRepository.findParticipantByRoomId(
            roomId);

        //then
        ParticipantJpaDto participantJpaDto1 = participants.get(0);
        ParticipantJpaDto participantJpaDto2 = participants.get(1);
        assertThat(participants).hasSize(2);
        assertThat(participantJpaDto1.userId()).isEqualTo(user1.getId());
        assertThat(participantJpaDto1.nickname()).isEqualTo(user1.getNickname());
        assertThat(participantJpaDto1.ageGroup()).isEqualTo(user1.getAgeGroup());
        assertThat(participantJpaDto1.isLeader()).isEqualTo(participant1.isLeader());

        assertThat(participantJpaDto2.userId()).isEqualTo(user2.getId());
        assertThat(participantJpaDto2.nickname()).isEqualTo(user2.getNickname());
        assertThat(participantJpaDto2.ageGroup()).isEqualTo(user2.getAgeGroup());
        assertThat(participantJpaDto2.isLeader()).isEqualTo(participant2.isLeader());
    }
}