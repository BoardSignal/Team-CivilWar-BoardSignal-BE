package com.civilwar.boardsignal.room.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.civilwar.boardsignal.common.support.DataJpaTestSupport;
import com.civilwar.boardsignal.room.MeetingInfoFixture;
import com.civilwar.boardsignal.room.RoomFixture;
import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RoomJpaRepositoryTest extends DataJpaTestSupport {

    @PersistenceUnit
    EntityManagerFactory emf;
    @Autowired
    private RoomJpaRepository roomJpaRepository;
    @Autowired
    private ParticipantJpaRepository participantJpaRepository;
    @Autowired
    private MeetingInfoJpaRepository meetingInfoJpaRepository;

    @Test
    @DisplayName("[유저는 자신이 참여한 모든 room을 조회하며, roomCategory와 meetingInfo를 fetch loading 한다.]")
    void findMyFixRoomTest() throws IOException {
        //given
        Long user1 = 1L;
        Long user2 = 2L;
        Room room = RoomFixture.getRoom();
        Room room2 = RoomFixture.getRoom();
        roomJpaRepository.save(room);
        roomJpaRepository.save(room2);

        //user1 -> room1 참여
        Participant participant = Participant.of(user1, room.getId(), true);
        //user1 -> room2 참여
        Participant participant2 = Participant.of(user1, room2.getId(), true);
        //user2 -> room2 참여
        Participant participant3 = Participant.of(user2, room2.getId(), false);
        participantJpaRepository.save(participant);
        participantJpaRepository.save(participant2);
        participantJpaRepository.save(participant3);

        //모임 확정
        MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(
            LocalDateTime.of(2024, 2, 22, 19, 0, 0));
        MeetingInfo meetingInfo2 = MeetingInfoFixture.getMeetingInfo(
            LocalDateTime.of(2024, 2, 22, 19, 0, 0));
        meetingInfoJpaRepository.save(meetingInfo);
        meetingInfoJpaRepository.save(meetingInfo2);

        room.fixRoom(meetingInfo);
        room2.fixRoom(meetingInfo2);
        roomJpaRepository.save(room);
        roomJpaRepository.save(room2);

        //when
        List<Room> userGame1 = roomJpaRepository.findMyFixRoom(user1);
        List<Room> userGame2 = roomJpaRepository.findMyFixRoom(user2);

        Room room1 = userGame1.get(0);
        //fetch loading 확인
        boolean loaded1 = emf.getPersistenceUnitUtil().isLoaded(room1.getRoomCategories().get(0));
        boolean loaded2 = emf.getPersistenceUnitUtil().isLoaded(room1.getMeetingInfo());

        //then
        assertThat(userGame1).hasSize(2);
        assertThat(userGame2).hasSize(1);
        assertThat(loaded1).isTrue();
        assertThat(loaded2).isTrue();
    }

    @Test
    @DisplayName("[자신이 참가한 30개의 room중, fix된 15개의 room만 갖고온다]")
    void findMyFixRoomTest2() throws IOException {
        //given
        Long user1 = 1L;

        for (int i = 0; i < 30; i++) {
            //방 생성
            Room room = RoomFixture.getRoom();
            roomJpaRepository.save(room);
            Participant participant = Participant.of(user1, room.getId(), true);
            participantJpaRepository.save(participant);

            if (i % 2 == 0) {
                //모임 확정
                MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(
                    LocalDateTime.of(2024, 2, 22, 19, 0, 0));
                meetingInfoJpaRepository.save(meetingInfo);
                room.fixRoom(meetingInfo);
                roomJpaRepository.save(room);
            }
        }

        //when
        List<Room> all = roomJpaRepository.findAll();
        List<Room> userGame1 = roomJpaRepository.findMyFixRoom(user1);

        //then
        assertThat(all).hasSize(30);
        assertThat(userGame1).hasSize(15);
    }
}