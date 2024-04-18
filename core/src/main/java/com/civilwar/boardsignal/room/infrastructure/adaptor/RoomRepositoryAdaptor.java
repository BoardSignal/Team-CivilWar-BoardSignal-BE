package com.civilwar.boardsignal.room.infrastructure.adaptor;

import static com.civilwar.boardsignal.room.domain.entity.QRoom.room;
import static com.civilwar.boardsignal.room.domain.entity.QRoomCategory.roomCategory;
import static org.springframework.util.StringUtils.hasText;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.room.domain.constants.DaySlot;
import com.civilwar.boardsignal.room.domain.constants.RoomStatus;
import com.civilwar.boardsignal.room.domain.constants.TimeSlot;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.room.dto.request.RoomSearchCondition;
import com.civilwar.boardsignal.room.dto.response.ChatRoomDto;
import com.civilwar.boardsignal.room.infrastructure.repository.RoomJpaRepository;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoomRepositoryAdaptor implements RoomRepository {

    private final RoomJpaRepository roomJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    private BooleanExpression equalsNonFixRoom() {
        return room.status.eq(RoomStatus.NON_FIX);
    }

    private BooleanExpression equalAllowedOppositeGender(String allowedGender) {
        if (allowedGender == null) {
            return null;
        }
        Gender genderType = Gender.toGender(allowedGender);

        return room.allowedGender.eq(genderType);
    }

    private BooleanExpression containsCategory(List<String> category) {
        if (category == null || category.isEmpty()) {
            return null;
        }

        List<Category> categoryList = category.stream()
            .map(Category::of)
            .toList();
        return roomCategory.category.in(categoryList);
    }

    private BooleanExpression containsAnyTime(List<String> times) {
        if (times == null || times.isEmpty()) {
            return null;
        }

        BooleanExpression[] booleanExpressions = times.stream()
            //1. 문자열 분리 / 주말_오전 -> (주말, 오전)
            .map(t -> t.split("_"))
            //2. 조건 비교
            .map(this::equalTime)
            .toArray(BooleanExpression[]::new);

        return Expressions.anyOf(booleanExpressions);
    }

    private BooleanExpression equalTime(String[] timeSplit) {
        return room.daySlot.eq(DaySlot.of(timeSplit[0]))
            .and(room.timeSlot.eq(TimeSlot.of(timeSplit[1])));
    }

    private BooleanExpression containsStation(List<String> station) {
        if (station == null || station.isEmpty()) {
            return null;
        }

        return room.subwayStation.in(station);
    }

    private BooleanExpression containsKeyword(String keyword) {
        if (!hasText(keyword)) {
            return null;
        }

        return room.title.contains(keyword)
            .or(room.description.contains(keyword));
    }

    @Override
    public Room save(Room room) {
        return roomJpaRepository.save(room);
    }

    @Override
    public void saveAll(Collection<Room> rooms) {
        roomJpaRepository.saveAll(rooms);
    }

    @Override
    public Optional<Room> findById(Long id) {
        return roomJpaRepository.findById(id);
    }

    @Override
    public Slice<ChatRoomDto> findMyChattingRoom(Long userId, LocalDateTime today, Pageable pageable) {
        return roomJpaRepository.findMyChattingRoom(userId, today, pageable);
    }

    @Override
    public Slice<Room> findMyEndRoomPaging(Long userId, LocalDateTime today, Pageable pageable) {
        return roomJpaRepository.findMyEndRoomPaging(userId, today, pageable);
    }

    @Override
    public int countByMyEndRoom(Long userId, LocalDateTime today) {
        return roomJpaRepository.countByMyEndRoom(userId, today);
    }

    @Override
    public Slice<Room> findAll(RoomSearchCondition roomSearchCondition, Pageable pageable) {
        boolean hasNext = false;

        List<Room> rooms = jpaQueryFactory
            .selectFrom(room)
            .join(room.roomCategories, roomCategory)
            .where(
                containsKeyword(roomSearchCondition.searchKeyword()),
                containsStation(roomSearchCondition.station()),
                containsAnyTime(roomSearchCondition.time()),
                containsCategory(roomSearchCondition.category()),
                equalAllowedOppositeGender(roomSearchCondition.gender()),
                equalsNonFixRoom()
            )
            .groupBy(room)
            .orderBy(room.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1L)
            .fetch();

        if (rooms.size() > pageable.getPageSize()) {
            hasNext = true;
            rooms.remove(rooms.size() - 1);
        }
        return new SliceImpl<>(rooms, pageable, hasNext);
    }

    @Override
    public Optional<Room> findByIdWithLock(Long id) {
        return roomJpaRepository.findByIdWithLock(id);
    }

    @Override
    public void deleteById(Long id) {
        roomJpaRepository.deleteById(id);
    }
}
