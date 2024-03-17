package com.civilwar.boardsignal.room.dto.mapper;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.room.domain.constants.DaySlot;
import com.civilwar.boardsignal.room.domain.constants.TimeSlot;
import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.dto.request.CreateRoomRequest;
import com.civilwar.boardsignal.room.dto.response.ChatRoomResponse;
import com.civilwar.boardsignal.room.dto.response.CreateRoomResponse;
import com.civilwar.boardsignal.room.dto.response.FixRoomResponse;
import com.civilwar.boardsignal.room.dto.response.GetAllRoomResponse;
import com.civilwar.boardsignal.room.dto.response.GetEndGameResponse;
import com.civilwar.boardsignal.room.dto.response.GetEndGameUsersResponse;
import com.civilwar.boardsignal.room.dto.response.ParticipantJpaDto;
import com.civilwar.boardsignal.room.dto.response.ParticipantResponse;
import com.civilwar.boardsignal.room.dto.response.RoomInfoResponse;
import com.civilwar.boardsignal.room.dto.response.RoomPageResponse;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoomMapper {

    public static Room toRoom(
        String roomImageUrl,
        CreateRoomRequest request,
        Gender allowedGender
    ) {
        DaySlot daySlot = DaySlot.of(request.day());
        TimeSlot timeSlot = TimeSlot.of(request.time());
        List<Category> categories = request.categories().stream()
            .map(Category::of)
            .toList();

        return Room.of(
            request.roomTitle(),
            request.description(),
            request.minParticipants(),
            request.maxParticipants(),
            request.place(),
            request.subwayLine(),
            request.subwayStation(),
            daySlot,
            timeSlot,
            request.startTime(),
            request.minAge(),
            request.maxAge(),
            roomImageUrl,
            allowedGender,
            categories
        );
    }

    public static CreateRoomResponse toCreateRoomResponse(Room room) {
        return new CreateRoomResponse(room.getId());
    }

    public static GetAllRoomResponse toGetAllRoomResponse(Room room) {
        List<String> categories = room.getRoomCategories().stream()
            .map(roomCategory -> roomCategory.getCategory().getDescription())
            .toList();
        String time =
            room.getDaySlot().getDescription() + " " + room.getTimeSlot().getDescription();
        String station = String.format("%s(%s)", room.getSubwayStation(), room.getSubwayLine());
        log.info("{}", station);

        return new GetAllRoomResponse(
            room.getId(),
            room.getTitle(),
            room.getDescription(),
            station,
            time,
            room.getMinAge(),
            room.getMaxAge(),
            room.getAllowedGender().getDescription(),
            room.getImageUrl(),
            room.getMinParticipants(),
            room.getMaxParticipants(),
            categories,
            room.getCreatedAt(),
            room.getHeadCount()
        );
    }

    public static GetEndGameResponse toGetEndGameResponse(Room room) {
        List<String> categories = room.getRoomCategories().stream()
            .map(roomCategory -> roomCategory.getCategory().getDescription())
            .toList();
        String time =
            room.getDaySlot().getDescription() + " " + room.getTimeSlot().getDescription();
        String station = String.format("%s(%s)", room.getSubwayStation(), room.getSubwayLine());
        log.info("{}", station);

        return new GetEndGameResponse(
            room.getId(),
            room.getTitle(),
            room.getDescription(),
            station,
            time,
            room.getMinAge(),
            room.getMaxAge(),
            room.getAllowedGender().getDescription(),
            room.getImageUrl(),
            room.getMinParticipants(),
            room.getMaxParticipants(),
            categories,
            room.getCreatedAt(),
            room.getHeadCount(),
            room.getMeetingInfo().getMeetingTime()
        );
    }

    public static <T> RoomPageResponse<T> toRoomPageResponse(Slice<T> pages) {

        return new RoomPageResponse<>(
            pages.getContent(),
            pages.getNumber(),
            pages.getSize(),
            pages.hasNext()
        );
    }

    public static RoomInfoResponse toRoomInfoResponse(
        Room room,
        String time,
        String startTime,
        String subwayLine,
        String subwayStation,
        String place,
        Boolean isLeader,
        List<ParticipantResponse> participants
    ) {
        return new RoomInfoResponse(
            room.getId(),
            room.getTitle(),
            room.getDescription(),
            time,
            startTime,
            subwayLine,
            subwayStation,
            place,
            room.getMinAge(),
            room.getMaxAge(),
            room.getMinParticipants(),
            room.getMaxParticipants(),
            room.getImageUrl(),
            isLeader,
            room.getStatus().getDescription(),
            room.getAllowedGender().getDescription(),
            room.getRoomCategories().stream()
                .map(roomCategory -> roomCategory.getCategory().getDescription())
                .toList(),
            participants,
            room.getCreatedAt()
        );
    }

    public static ParticipantResponse toParticipantResponse(ParticipantJpaDto participantJpaDto) {
        return new ParticipantResponse(
            participantJpaDto.userId(),
            participantJpaDto.nickname(),
            participantJpaDto.ageGroup().getDescription(),
            participantJpaDto.profileImageUrl(),
            participantJpaDto.isLeader(),
            participantJpaDto.mannerScore()
        );
    }

    public static FixRoomResponse toFixRoomResponse(Room room, MeetingInfo meetingInfo) {
        return new FixRoomResponse(room.getId(), meetingInfo.getId());
    }

    public static GetEndGameUsersResponse toGetEndGameUserResponse(
        Room room,
        List<ParticipantResponse> participants
    ) {
        MeetingInfo meetingInfo = room.getMeetingInfo();
        List<String> categories = room.getRoomCategories().stream()
            .map(roomCategory -> roomCategory.getCategory().getDescription())
            .toList();

        return new GetEndGameUsersResponse(
            room.getId(),
            room.getTitle(),
            meetingInfo.getMeetingTime(),
            meetingInfo.getPeopleCount(),
            meetingInfo.getLine(),
            meetingInfo.getStation(),
            meetingInfo.getMeetingPlace(),
            room.getAllowedGender().getDescription(),
            room.getMinAge(),
            room.getMaxAge(),
            room.getMinParticipants(),
            room.getMaxParticipants(),
            categories,
            room.getCreatedAt(),
            room.getHeadCount(),
            participants
        );
    }

    public static ChatRoomResponse toChatRoomResponse(Room room) {
        return new ChatRoomResponse(
            room.getId(),
            room.getTitle(),
            room.getImageUrl(),
            room.getHeadCount());
    }
}
