package com.civilwar.boardsignal.room.facade;

import com.civilwar.boardsignal.notification.domain.constant.NotificationContent;
import com.civilwar.boardsignal.notification.dto.request.NotificationRequest;
import com.civilwar.boardsignal.room.application.RoomService;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.dto.mapper.RoomMapper;
import com.civilwar.boardsignal.room.dto.request.CreateRoomRequest;
import com.civilwar.boardsignal.room.dto.request.FixRoomRequest;
import com.civilwar.boardsignal.room.dto.request.KickOutUserRequest;
import com.civilwar.boardsignal.room.dto.request.RoomSearchCondition;
import com.civilwar.boardsignal.room.dto.response.ChatRoomResponse;
import com.civilwar.boardsignal.room.dto.response.CreateRoomResponse;
import com.civilwar.boardsignal.room.dto.response.DeleteRoomFacadeResponse;
import com.civilwar.boardsignal.room.dto.response.ExitRoomResponse;
import com.civilwar.boardsignal.room.dto.response.FixRoomResponse;
import com.civilwar.boardsignal.room.dto.response.GetAllRoomResponse;
import com.civilwar.boardsignal.room.dto.response.GetEndGameResponse;
import com.civilwar.boardsignal.room.dto.response.GetEndGameUsersResponse;
import com.civilwar.boardsignal.room.dto.response.KickOutFacadeResponse;
import com.civilwar.boardsignal.room.dto.response.KickOutResponse;
import com.civilwar.boardsignal.room.dto.response.ParticipantJpaDto;
import com.civilwar.boardsignal.room.dto.response.ParticipantRoomResponse;
import com.civilwar.boardsignal.room.dto.response.RoomInfoResponse;
import com.civilwar.boardsignal.room.dto.response.RoomPageResponse;
import com.civilwar.boardsignal.user.application.UserService;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomFacade {

    private final UserService userService;
    private final RoomService roomService;
    private final ApplicationEventPublisher publisher;

    public CreateRoomResponse createRoom(
        User user,
        CreateRoomRequest request
    ) {
        CreateRoomResponse roomResponse = roomService.createRoom(user, request);
        List<Long> userIds = userService.getUserByStation(request.subwayStation())
            .stream()
            .map(User::getId)
            .toList();

        // 새로 생긴 방의 지하철역에 해당하는 유저들에게 알림 전송
        NotificationRequest notificationRequest = new NotificationRequest(
            NotificationContent.ROOM_CREATED_NEARLY.getTitle(),
            NotificationContent.ROOM_CREATED_NEARLY.getMessage(request.subwayStation()),
            null,
            roomResponse.roomId(),
            userIds
        );

        publisher.publishEvent(notificationRequest);

        return roomResponse;
    }

    public ParticipantRoomResponse participateRoom(
        User user,
        Long roomId
    ) {
        return roomService.participateRoom(user, roomId);
    }

    public ExitRoomResponse exitRoom(
        User user,
        Long roomId
    ) {
        return roomService.exitRoom(user, roomId);
    }

    public RoomPageResponse<ChatRoomResponse> findMyGame(
        User user,
        Pageable pageable
    ) {
        return roomService.findMyGame(user, pageable);
    }

    public RoomPageResponse<GetEndGameResponse> findMyEndGame(
        Long userId,
        Pageable pageable
    ) {
        return roomService.findMyEndGame(userId, pageable);
    }

    public RoomPageResponse<GetAllRoomResponse> findRoomBySearch(
        RoomSearchCondition roomSearchCondition,
        Pageable pageable
    ) {
        return roomService.findRoomBySearch(roomSearchCondition, pageable);
    }

    public RoomInfoResponse findRoomInfo(User user, Long roomId) {
        return roomService.findRoomInfo(user, roomId);
    }

    public FixRoomResponse fixRoom(
        User user,
        Long roomId,
        FixRoomRequest request
    ) {
        Room room = roomService.fixRoom(user, roomId, request);

        List<ParticipantJpaDto> participants = roomService.getParticipants(roomId);
        //해당 방의 참여자들 id
        List<Long> userIds = participants.stream()
            .map(ParticipantJpaDto::userId)
            .toList();

        NotificationRequest notificationRequest = new NotificationRequest(
            NotificationContent.ROOM_FIXED.getTitle(),
            NotificationContent.ROOM_FIXED.getMessage(room.getTitle()),
            room.getImageUrl(),
            room.getId(),
            userIds
        );
        publisher.publishEvent(notificationRequest);

        return RoomMapper.toFixRoomResponse(room, room.getMeetingInfo());
    }

    public GetEndGameUsersResponse getEndGameUsersResponse(User user, Long roomId) {
        return roomService.getEndGameUsersResponse(user, roomId);
    }

    public void unFixRoom(User user, Long roomId) {
        roomService.unFixRoom(user, roomId);
    }

    public void deleteRoom(User user, Long roomId) {
        DeleteRoomFacadeResponse response = roomService.deleteRoom(user, roomId);

        List<ParticipantJpaDto> participants = response.participants();
        Room room = response.room();

        // 삭제된 방에 참여했던 참여자들 id
        List<Long> userIds = participants.stream()
            .map(ParticipantJpaDto::userId)
            .toList();

        NotificationRequest notificationRequest = new NotificationRequest(
            NotificationContent.ROOM_REMOVED.getTitle(),
            NotificationContent.ROOM_REMOVED.getMessage(room.getTitle()),
            room.getImageUrl(),
            null,
            userIds
        );

        publisher.publishEvent(notificationRequest);
    }

    public KickOutResponse kickOutUser(User leader, KickOutUserRequest kickOutUserRequest) {
        KickOutFacadeResponse kickOutFacadeResponse = roomService.kickOutUser(leader, kickOutUserRequest);

        Room room = kickOutFacadeResponse.room();
        String kickOutUserNickname = kickOutFacadeResponse.kickOutUserNickname();

        NotificationRequest notificationRequest = new NotificationRequest(
            NotificationContent.KICKED_FROM_ROOM.getTitle(),
            NotificationContent.KICKED_FROM_ROOM.getMessage(room.getTitle()),
            room.getImageUrl(),
            null,
            List.of(leader.getId())
        );

        publisher.publishEvent(notificationRequest);

        return new KickOutResponse(kickOutUserNickname);
    }

}
