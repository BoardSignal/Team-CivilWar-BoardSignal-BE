package com.civilwar.boardsignal.room.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.common.support.DataJpaTestSupport;
import com.civilwar.boardsignal.room.MeetingInfoFixture;
import com.civilwar.boardsignal.room.RoomFixture;
import com.civilwar.boardsignal.room.domain.constants.DaySlot;
import com.civilwar.boardsignal.room.domain.constants.TimeSlot;
import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.room.dto.request.RoomSearchCondition;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

class RoomJpaRepositoryTest extends DataJpaTestSupport {

    private final String title = "달무티 할 사람";
    private final String description = "20대만";
    private final String station = "사당역";
    private final DaySlot daySlot = DaySlot.WEEKDAY;
    private final TimeSlot timeSlot = TimeSlot.AM;
    private final List<Category> categories = List.of(Category.FAMILY, Category.PARTY);
    @PersistenceUnit
    EntityManagerFactory emf;
    @Autowired
    private RoomRepository roomRepository;
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
        Room room = RoomFixture.getRoom(Gender.UNION);
        Room room2 = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room);
        roomRepository.save(room2);

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
        roomRepository.save(room);
        roomRepository.save(room2);

        //when
        List<Room> userGame1 = roomRepository.findMyFixRoom(user1);
        List<Room> userGame2 = roomRepository.findMyFixRoom(user2);

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
            Room room = RoomFixture.getRoom(Gender.UNION);
            roomRepository.save(room);
            Participant participant = Participant.of(user1, room.getId(), true);
            participantJpaRepository.save(participant);

            if (i % 2 == 0) {
                //모임 확정
                MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(
                    LocalDateTime.of(2024, 2, 22, 19, 0, 0));
                meetingInfoJpaRepository.save(meetingInfo);
                room.fixRoom(meetingInfo);
                roomRepository.save(room);
            }
        }

        //when
        List<Room> userGame1 = roomRepository.findMyFixRoom(user1);

        //then
        assertThat(userGame1).hasSize(15);
    }

    @Test
    @DisplayName("[자신이 참가한 모임 조회 시, 모임 확정일 기준 역순으로 정렬된다]")
    void findMyFixRoomTest3() throws IOException {
        //given
        Long user1 = 1L;

        LocalDateTime before = LocalDateTime.of(2024, 2, 21, 19, 0, 0);
        LocalDateTime now = LocalDateTime.of(2024, 2, 23, 19, 0, 0);
        LocalDateTime after = LocalDateTime.of(2024, 2, 25, 19, 0, 0);

        LocalDateTime[] times = new LocalDateTime[] {
            before, now, after
        };

        for (LocalDateTime time : times) {
            //방 생성
            Room room = RoomFixture.getRoom(Gender.UNION);
            roomRepository.save(room);
            Participant participant = Participant.of(user1, room.getId(), true);
            participantJpaRepository.save(participant);
            //모임 확정
            MeetingInfo meetingInfo = MeetingInfoFixture.getMeetingInfo(time);
            meetingInfoJpaRepository.save(meetingInfo);
            room.fixRoom(meetingInfo);
            roomRepository.save(room);
        }

        //when
        List<Room> fixGame = roomRepository.findMyFixRoom(user1);

        Room room = fixGame.get(0);
        LocalDateTime meetingTime1 = room.getMeetingInfo().getMeetingTime();
        Room room1 = fixGame.get(1);
        LocalDateTime meetingTime2 = room1.getMeetingInfo().getMeetingTime();
        Room room2 = fixGame.get(2);
        LocalDateTime meetingTime3 = room2.getMeetingInfo().getMeetingTime();

        //then
        assertThat(fixGame).hasSize(3);
        assertThat(meetingTime1).isEqualTo(after);
        assertThat(meetingTime2).isEqualTo(now);
        assertThat(meetingTime3).isEqualTo(before);
    }

    @Test
    @DisplayName("[키워드 검색에 맞는 방을 갖고 온다.]")
    void findAllTest1() {
        //given
        List<String> titles = List.of("달무티", "달무티 3", "아발론", "달무티2");
        String keyword = "달무";
        String keyword2 = "20대";
        for (String s : titles) {
            Room room = RoomFixture.getAnotherRoom(
                s,
                description,
                station,
                daySlot,
                timeSlot,
                categories,
                Gender.UNION
            );
            roomRepository.save(room);
        }

        //keyword 달무티 -> 3개
        RoomSearchCondition roomSearchCondition1 = new RoomSearchCondition(
            keyword,
            null,
            null,
            null,
            null
        );
        PageRequest pageRequest1 = PageRequest.of(0, 2);

        //keyword 20대 -> 1개
        RoomSearchCondition roomSearchCondition2 = new RoomSearchCondition(
            keyword2,
            null,
            null,
            null,
            null
        );
        PageRequest pageRequest2 = PageRequest.of(0, 5);

        //when
        Slice<Room> result1 = roomRepository.findAll(roomSearchCondition1, pageRequest1);
        Slice<Room> result2 = roomRepository.findAll(roomSearchCondition2, pageRequest2);

        //then
        //달무 검색 -> 3개 중 2개 slice
        assertThat(result1.getContent()).hasSize(2);
        assertThat(result1.hasNext()).isTrue();

        //20대 검색 -> 4개
        assertThat(result2.getContent()).hasSize(4);
        assertThat(result2.hasNext()).isFalse();
    }

    @Test
    @DisplayName("[동성만 입장 허용하는 방을 검색한다]")
    void findAllTest2() {
        //given
        for (int i = 0; i < 50; i++) {
            Room room = RoomFixture.getAnotherRoom(
                title,
                description,
                station,
                daySlot,
                timeSlot,
                categories,
                Gender.UNION
            );
            roomRepository.save(room);
        }

        //동성만 허용
        RoomSearchCondition roomSearchCondition1 = new RoomSearchCondition(
            null,
            null,
            null,
            null,
            Gender.UNION.getDescription()
        );
        PageRequest pageRequest1 = PageRequest.of(0, 50);

        //when
        Slice<Room> result1 = roomRepository.findAll(roomSearchCondition1, pageRequest1);

        //then
        //동성만 허용 50개
        assertThat(result1.getContent()).hasSize(50);
        assertThat(result1.hasNext()).isFalse();
    }

    @Test
    @DisplayName("[조건에 맞는 장소를 가진 모임을 갖고온다.]")
    void findAllTest3() {
        //given
        List<String> stations = List.of("사당역", "언주역", "이수역", "강남역");
        for (String s : stations) {
            Room room = RoomFixture.getAnotherRoom(
                title,
                description,
                s,
                daySlot,
                timeSlot,
                categories,
                Gender.UNION
            );
            roomRepository.save(room);
        }

        RoomSearchCondition condition = new RoomSearchCondition(
            null,
            List.of("사당역", "언주역"),
            null,
            null,
            null
        );
        PageRequest pageRequest1 = PageRequest.of(0, 50);

        //when
        Slice<Room> all = roomRepository.findAll(condition, pageRequest1);

        //then
        assertThat(all.getContent()).hasSize(2);
        assertThat(all.hasNext()).isFalse();
    }

    @Test
    @DisplayName("[조건에 맞는 시간을 가진 모임을 갖고온다.]")
    void findAllTest4() {
        //given
        List<DaySlot> day = List.of(
            DaySlot.WEEKDAY,
            DaySlot.WEEKDAY,
            DaySlot.WEEKEND,
            DaySlot.WEEKDAY,
            DaySlot.WEEKEND);
        List<TimeSlot> time = List.of(
            TimeSlot.AM,
            TimeSlot.AM,
            TimeSlot.PM,
            TimeSlot.PM,
            TimeSlot.AM
        );
        for (int i = 0; i < day.size(); i++) {
            Room room = RoomFixture.getAnotherRoom(
                title,
                description,
                station,
                day.get(i),
                time.get(i),
                categories,
                Gender.UNION
            );
            roomRepository.save(room);
        }

        RoomSearchCondition condition1 = new RoomSearchCondition(
            null,
            null,
            List.of("평일_오전"),
            null,
            null
        );
        RoomSearchCondition condition2 = new RoomSearchCondition(
            null,
            null,
            List.of("평일_오전", "주말_오후"),
            null,
            null
        );
        PageRequest pageRequest1 = PageRequest.of(0, 50);

        //when
        Slice<Room> all1 = roomRepository.findAll(condition1, pageRequest1);
        Slice<Room> all2 = roomRepository.findAll(condition2, pageRequest1);

        //then
        assertThat(all1.getContent()).hasSize(2);
        assertThat(all2.getContent()).hasSize(3);
    }

    @Test
    @DisplayName("[조건에 맞는 카테고리가 일부 들어있는 모임을 갖고온다.]")
    void findAllTest5() {
        //given
        List<List<Category>> categoryLists = List.of(
            List.of(Category.FAMILY, Category.PARTY),
            List.of(Category.WAR, Category.PARTY),
            List.of(Category.CUSTOMIZABLE, Category.FAMILY),
            List.of(Category.CHILDREN, Category.STRATEGY),
            List.of(Category.THEMATIC, Category.CHILDREN)
        );
        for (List<Category> categoryList : categoryLists) {
            Room room = RoomFixture.getAnotherRoom(
                title,
                description,
                station,
                daySlot,
                timeSlot,
                categoryList,
                Gender.UNION
            );
            roomRepository.save(room);
        }

        RoomSearchCondition condition1 = new RoomSearchCondition(
            null,
            null,
            null,
            List.of("가족게임", "파티게임"),
            null
        );
        RoomSearchCondition condition2 = new RoomSearchCondition(
            null,
            null,
            null,
            List.of("테마게임", "추상게임"),
            null
        );
        PageRequest pageRequest1 = PageRequest.of(0, 50);

        //when
        Slice<Room> all = roomRepository.findAll(condition1, pageRequest1);
        Slice<Room> all2 = roomRepository.findAll(condition2, pageRequest1);

        //then
        assertThat(all.getContent()).hasSize(3);
        assertThat(all2.getContent()).hasSize(1);

    }
}