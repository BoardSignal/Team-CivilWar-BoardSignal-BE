package com.civilwar.boardsignal.room.application;

import static com.civilwar.boardsignal.room.exception.RoomErrorCode.INVALID_PARTICIPANT;
import static com.civilwar.boardsignal.room.exception.RoomErrorCode.IS_NOT_LEADER;
import static com.civilwar.boardsignal.room.exception.RoomErrorCode.NOT_FOUND_ROOM;

import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.common.exception.ValidationException;
import com.civilwar.boardsignal.image.domain.ImageRepository;
import com.civilwar.boardsignal.room.domain.constants.RoomStatus;
import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.repository.MeetingInfoRepository;
import com.civilwar.boardsignal.room.domain.repository.ParticipantRepository;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.room.dto.mapper.RoomMapper;
import com.civilwar.boardsignal.room.dto.request.CreateRoomResponse;
import com.civilwar.boardsignal.room.dto.request.FixRoomRequest;
import com.civilwar.boardsignal.room.dto.request.KickOutUserRequest;
import com.civilwar.boardsignal.room.dto.request.RoomSearchCondition;
import com.civilwar.boardsignal.room.dto.response.CreateRoomRequest;
import com.civilwar.boardsignal.room.dto.response.ExitRoomResponse;
import com.civilwar.boardsignal.room.dto.response.FixRoomResponse;
import com.civilwar.boardsignal.room.dto.response.GetAllRoomResponse;
import com.civilwar.boardsignal.room.dto.response.GetEndGameUsersResponse;
import com.civilwar.boardsignal.room.dto.response.ParticipantResponse;
import com.civilwar.boardsignal.room.dto.response.ParticipantRoomResponse;
import com.civilwar.boardsignal.room.dto.response.RoomInfoResponse;
import com.civilwar.boardsignal.room.dto.response.RoomPageResponse;
import com.civilwar.boardsignal.room.exception.RoomErrorCode;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final MeetingInfoRepository meetingInfoRepository;
    private final ImageRepository imageRepository;
    private final Supplier<LocalDateTime> now;

    private static String concat(String string1, String string2) {
        return string1
            + " "
            + string2;
    }

    @Transactional
    public CreateRoomResponse createRoom(User user, CreateRoomRequest request) {
        String roomImageUrl = imageRepository.save(request.image());
        Gender allowedGender = user.getGender();
        //이성의 입장을 허용한다면
        if (request.isAllowedOppositeGender()) {
            allowedGender = Gender.UNION;
        }

        Room room = RoomMapper.toRoom(roomImageUrl, request, allowedGender);
        Room savedRoom = roomRepository.save(room);

        Participant participant = Participant.of(
            user.getId(),
            savedRoom.getId(),
            true // 방 생성자가 방장여부는 true
        ); // 나중에 방 폭파 때 해당 방의 Participant 전부 삭제
        participantRepository.save(participant);

        return RoomMapper.toCreateRoomResponse(savedRoom);
    }

    @Transactional
    public ParticipantRoomResponse participateRoom(User user, Long roomId) {

        //참여 여부 확인
        if (participantRepository.existsByUserIdAndRoomId(user.getId(), roomId)) {
            throw new ValidationException(RoomErrorCode.ALREADY_PARTICIPANT);
        }

        //참여 인원 수 증가
        Room findRoom = roomRepository.findByIdWithLock(roomId)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));
        findRoom.increaseHeadCount();

        //참여 정보 저장
        Participant participant = Participant.of(user.getId(), roomId, false);
        participantRepository.save(participant);

        return new ParticipantRoomResponse(findRoom.getHeadCount());
    }

    @Transactional
    public ExitRoomResponse exitRoom(User user, Long roomId) {

        //참여 여부 확인 -> 참여하고 있지 않다면 예외
        if (!participantRepository.existsByUserIdAndRoomId(user.getId(), roomId)) {
            throw new NotFoundException(INVALID_PARTICIPANT);
        }

        //참여 인원 수 감소
        Room findRoom = roomRepository.findByIdWithLock(roomId)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));
        findRoom.decreaseHeadCount();

        //참여 정보 삭제
        participantRepository.deleteByUserIdAndRoomId(user.getId(), roomId);

        return new ExitRoomResponse(findRoom.getHeadCount());
    }

    @Transactional(readOnly = true)
    public RoomPageResponse<GetAllRoomResponse> findMyEndGame(
        Long userId,
        Pageable pageable
    ) {

        boolean hasNext = false;

        //1. 내가 참여한 모든 room
        List<Room> myFixGame = roomRepository.findMyFixRoom(userId);

        //2. (모임 확정 day) < 현재 day 인 room
        List<Room> myEndGame = new ArrayList<>(
            myFixGame.stream()
                .filter(room -> room.getMeetingInfo().getMeetingTime().toLocalDate()
                    .isBefore(now.get().toLocalDate())
                ).toList()
        );

        //3. 내가 한 게임 전체 size > 요구 size -> 다음 페이지 존재
        if (myEndGame.size() > pageable.getPageSize()) {
            hasNext = true;
        }

        //4. size 크기만큼 cut
        List<Room> resultList = myEndGame.stream()
            .limit(pageable.getPageSize())
            .toList();

        //5. slice 변환
        Slice<Room> result = new SliceImpl<>(resultList, pageable, hasNext);

        return RoomMapper.toRoomPageResponse(result);
    }

    @Transactional(readOnly = true)
    public RoomPageResponse<GetAllRoomResponse> findRoomBySearch(
        RoomSearchCondition roomSearchCondition,
        Pageable pageable
    ) {
        Slice<Room> findRooms = roomRepository.findAll(roomSearchCondition, pageable);
        return RoomMapper.toRoomPageResponse(findRooms);
    }

    @Transactional(readOnly = true)
    public RoomInfoResponse findRoomInfo(User user, Long roomId) {
        //1. 모임 정보 & 모임 확정 정보 (MeetingInfo)
        Room findRoom = roomRepository.findById(roomId)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));

        //1-1. 모임 시간 & 장소 정보 추출
        String resultPlace = concat(findRoom.getSubwayStation(), findRoom.getPlaceName());
        String resultTime = concat(findRoom.getDaySlot().getDescription(),
            findRoom.getTimeSlot().getDescription());

        //2. 모임 확정 여부 확인
        //모임 확정이라면 -> 모임 확정 시간 장소 정보로 제공
        if (findRoom.getStatus().equals(RoomStatus.FIX)) {
            MeetingInfo meetingInfo = findRoom.getMeetingInfo();
            resultTime = meetingInfo.getMeetingTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            resultPlace = concat(meetingInfo.getStation(), meetingInfo.getMeetingPlace());
        }

        //3. 방 참가자 정보
        List<ParticipantResponse> participants = participantRepository.findParticipantByRoomId(
                roomId)
            .stream()
            .map(RoomMapper::toParticipantResponse)
            .toList();

        //4. 로그인 한 사용자인지 확인

        //현재 로그인 상태가 아니라면 -> leaderX
        Boolean isLeader = false;

        //현재 로그인 상태라면 -> leader 확인
        if (user != null) {
            //4. 현재 사용자의 방장 여부 확인
            isLeader = participants.stream()
                .filter(participant -> participant.userId().equals(user.getId()))
                .map(ParticipantResponse::isLeader)
                .findAny()
                .orElse(false);
        }

        return RoomMapper.toRoomInfoResponse(findRoom, resultTime, resultPlace, isLeader,
            participants);
    }

    @Transactional
    public FixRoomResponse fixRoom(
        User user,
        Long roomId,
        FixRoomRequest request
    ) {
        //방에 존재하는 참가자 인 지 검증
        Participant participant = participantRepository.findByUserIdAndRoomId(user.getId(), roomId)
            .orElseThrow(() -> new NotFoundException(INVALID_PARTICIPANT));

        //방장인 지 검증
        if (!participant.isLeader()) {
            throw new ValidationException(IS_NOT_LEADER);
        }

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));

        MeetingInfo meetingInfo = MeetingInfo.of(
            request.meetingTime(),
            request.weekDay(),
            request.peopleCount(),
            request.line(),
            request.station(),
            request.meetingPlace()
        );

        MeetingInfo savedMeetingInfo = meetingInfoRepository.save(meetingInfo);
        room.fixRoom(savedMeetingInfo);

        return RoomMapper.toFixRoomResponse(room, room.getMeetingInfo());
    }

    @Transactional(readOnly = true)
    public GetEndGameUsersResponse getEndGameUsersResponse(User user, Long roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));

        List<ParticipantResponse> participants = participantRepository.findParticipantByRoomId(
                roomId)
            .stream()
            //본인 제외
            .filter(participant -> !Objects.equals(participant.userId(), user.getId()))
            .map(RoomMapper::toParticipantResponse)
            .toList();

        return RoomMapper.toGetEndGameUserResponse(room, participants);
    }
  
    @Transactional
    public void unFixRoom(User user, Long roomId) {
        //방에 존재하는 참가자 인 지 검증
        boolean isParticipant = participantRepository.existsByUserIdAndRoomId(user.getId(), roomId);
        if (!isParticipant) {
            throw new NotFoundException(INVALID_PARTICIPANT);
        }

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));

        MeetingInfo meetingInfo = room.getMeetingInfo();

        meetingInfoRepository.deleteById(meetingInfo.getId());
        room.unFixRoom();
    }

    @Transactional
    public void kickOutUser(User leader, KickOutUserRequest kickOutUserRequest) {
        //방장 여부 확인
        Participant leaderInfo = participantRepository.findByUserIdAndRoomId(leader.getId(),
                kickOutUserRequest.roomId())
            .orElseThrow(() -> new ValidationException(IS_NOT_LEADER));

        //방장이 아니라면 불가
        if(!leaderInfo.isLeader()) {
            throw new ValidationException(IS_NOT_LEADER);
        }

        //추방자 정보
        Participant kickOutUser = participantRepository.findByUserIdAndRoomId(
                kickOutUserRequest.userId(),
                kickOutUserRequest.roomId())
            .orElseThrow(() -> new ValidationException(INVALID_PARTICIPANT));

        //추방
        participantRepository.deleteById(kickOutUser.getId());
    }
}
