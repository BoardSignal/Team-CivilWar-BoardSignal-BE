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
import com.civilwar.boardsignal.room.dto.response.ChatRoomDto;
import com.civilwar.boardsignal.user.domain.constants.Gender;
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

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ParticipantJpaRepository participantJpaRepository;
    @Autowired
    private MeetingInfoJpaRepository meetingInfoJpaRepository;

    @Test
    @DisplayName("[유저는 자신이 참여한 모임 중, 미확정이거나 확정 시간이 (오늘~미래)인 모임을 조회할 수 있다.]")
    void findMyChattingRoomTest() throws IOException {
        //given
        Long user = 1L;

        //유저가 참여하지 않은 방
        Room notParticipant = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(notParticipant);
        //미확정 방 -> 조회
        Room nonFixRoom = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(nonFixRoom);
        //확정 시간이 어제인 방
        Room pastRoom = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(pastRoom);
        //확정 시간이 오늘날짜인 방 -> 조회
        Room todayRoom = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(todayRoom);
        //확정 시간이 내일인 방 -> 조회
        Room futureRoom = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(futureRoom);

        //유저 참여
        Participant participant = Participant.of(user, nonFixRoom.getId(), false);
        participantJpaRepository.save(participant);
        Participant participant1 = Participant.of(user, pastRoom.getId(), false);
        participantJpaRepository.save(participant1);
        Participant participant2 = Participant.of(user, todayRoom.getId(), false);
        participantJpaRepository.save(participant2);
        Participant participant3 = Participant.of(user, futureRoom.getId(), false);
        participantJpaRepository.save(participant3);

        //모임 확정
        //어제
        MeetingInfo yesterdayFix = MeetingInfoFixture.getMeetingInfo(
            LocalDateTime.of(2024, 4, 10, 0, 0, 0));
        meetingInfoJpaRepository.save(yesterdayFix);
        pastRoom.fixRoom(yesterdayFix);
        roomRepository.save(pastRoom);
        //오늘
        MeetingInfo todayFix = MeetingInfoFixture.getMeetingInfo(
            LocalDateTime.of(2024, 4, 11, 19, 0, 0));
        meetingInfoJpaRepository.save(todayFix);
        todayRoom.fixRoom(todayFix);
        roomRepository.save(todayRoom);
        //내일
        MeetingInfo tomorrowFix = MeetingInfoFixture.getMeetingInfo(
            LocalDateTime.of(2024, 4, 12, 4, 0, 0));
        meetingInfoJpaRepository.save(tomorrowFix);
        futureRoom.fixRoom(tomorrowFix);
        roomRepository.save(futureRoom);

        //when
        LocalDateTime today = LocalDateTime.of(2024, 4, 11, 0, 0, 0);
        PageRequest pageable = PageRequest.of(0, 5);

        Slice<ChatRoomDto> myChattingRoom = roomRepository.findMyChattingRoom(user, today, pageable);
        List<ChatRoomDto> content = myChattingRoom.getContent();

        //then
        assertThat(content).hasSize(3);
        //최신 참여 순 정렬
        assertThat(content.get(0).id()).isEqualTo(futureRoom.getId());
        assertThat(content.get(1).id()).isEqualTo(todayRoom.getId());
        assertThat(content.get(2).id()).isEqualTo(nonFixRoom.getId());
    }

    @Test
    @DisplayName("[유저는 자신이 어제까지 참여한 종료된 모임을 갖고온다]")
    void findMyEndRoomPagingTest() throws IOException {
        /*
        room1 -> 미참여
        room2 -> 참여, non-fix
        room3 -> 참여, fix, 확정 시간 어제 -> 조회
        room4 -> 참여, fix, 확정 시간 오늘
        room5 -> 참여, fix, 확정 시간 내일
         */

        //given
        Long user = 1L;

        //미참여방
        Room notParticipant = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(notParticipant);
        //미확정 방
        Room nonFixRoom = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(nonFixRoom);
        //확정 시간이 어제인 방 -> 조회
        Room pastRoom = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(pastRoom);
        //확정 시간이 오늘날짜인 방
        Room todayRoom = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(todayRoom);
        //확정 시간이 내일인 방
        Room futureRoom = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(futureRoom);

        //유저 참여
        Participant participant = Participant.of(user, nonFixRoom.getId(), false);
        participantJpaRepository.save(participant);
        Participant participant1 = Participant.of(user, pastRoom.getId(), false);
        participantJpaRepository.save(participant1);
        Participant participant2 = Participant.of(user, todayRoom.getId(), false);
        participantJpaRepository.save(participant2);
        Participant participant3 = Participant.of(user, futureRoom.getId(), false);
        participantJpaRepository.save(participant3);

        //모임 확정
        //어제
        MeetingInfo yesterdayFix = MeetingInfoFixture.getMeetingInfo(
            LocalDateTime.of(2024, 4, 10, 0, 0, 0));
        meetingInfoJpaRepository.save(yesterdayFix);
        pastRoom.fixRoom(yesterdayFix);
        roomRepository.save(pastRoom);
        //오늘
        MeetingInfo todayFix = MeetingInfoFixture.getMeetingInfo(
            LocalDateTime.of(2024, 4, 11, 19, 0, 0));
        meetingInfoJpaRepository.save(todayFix);
        todayRoom.fixRoom(todayFix);
        roomRepository.save(todayRoom);
        //내일
        MeetingInfo tomorrowFix = MeetingInfoFixture.getMeetingInfo(
            LocalDateTime.of(2024, 4, 12, 4, 0, 0));
        meetingInfoJpaRepository.save(tomorrowFix);
        futureRoom.fixRoom(tomorrowFix);
        roomRepository.save(futureRoom);

        //when
        LocalDateTime today = LocalDateTime.of(2024, 4, 11, 0, 0, 0);
        PageRequest pageable = PageRequest.of(0, 5);

        Slice<Room> myChattingRoom = roomRepository.findMyEndRoomPaging(user, today, pageable);
        List<Room> content = myChattingRoom.getContent();

        //then
        assertThat(content).hasSize(1);
        assertThat(content.get(0).getId()).isEqualTo(pastRoom.getId());
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