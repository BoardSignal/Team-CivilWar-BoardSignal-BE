package com.civilwar.boardsignal.room.application;

import static com.civilwar.boardsignal.room.exception.RoomErrorCode.INVALID_DATE;
import static com.civilwar.boardsignal.room.exception.RoomErrorCode.INVALID_PARTICIPANT;
import static com.civilwar.boardsignal.room.exception.RoomErrorCode.IS_NOT_LEADER;
import static com.civilwar.boardsignal.room.exception.RoomErrorCode.NOT_FOUND_ROOM;

import com.civilwar.boardsignal.chat.domain.repository.ChatMessageRepository;
import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.common.exception.ValidationException;
import com.civilwar.boardsignal.image.domain.ImageRepository;
import com.civilwar.boardsignal.review.domain.entity.Review;
import com.civilwar.boardsignal.review.domain.repository.ReviewRepository;
import com.civilwar.boardsignal.room.domain.constants.RoomStatus;
import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.entity.RoomBlackList;
import com.civilwar.boardsignal.room.domain.repository.MeetingInfoRepository;
import com.civilwar.boardsignal.room.domain.repository.ParticipantRepository;
import com.civilwar.boardsignal.room.domain.repository.RoomBlackListRepository;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.room.dto.mapper.RoomMapper;
import com.civilwar.boardsignal.room.dto.request.CreateRoomRequest;
import com.civilwar.boardsignal.room.dto.request.FixRoomRequest;
import com.civilwar.boardsignal.room.dto.request.KickOutUserRequest;
import com.civilwar.boardsignal.room.dto.request.RoomSearchCondition;
import com.civilwar.boardsignal.room.dto.response.ChatRoomResponse;
import com.civilwar.boardsignal.room.dto.response.CreateRoomResponse;
import com.civilwar.boardsignal.room.dto.response.DeleteRoomFacadeResponse;
import com.civilwar.boardsignal.room.dto.response.ExitRoomResponse;
import com.civilwar.boardsignal.room.dto.response.GetAllRoomResponse;
import com.civilwar.boardsignal.room.dto.response.GetEndGameResponse;
import com.civilwar.boardsignal.room.dto.response.GetEndGameUsersResponse;
import com.civilwar.boardsignal.room.dto.response.KickOutFacadeResponse;
import com.civilwar.boardsignal.room.dto.response.ParticipantJpaDto;
import com.civilwar.boardsignal.room.dto.response.ParticipantResponse;
import com.civilwar.boardsignal.room.dto.response.ParticipantRoomResponse;
import com.civilwar.boardsignal.room.dto.response.RoomInfoResponse;
import com.civilwar.boardsignal.room.dto.response.RoomPageResponse;
import com.civilwar.boardsignal.room.exception.RoomErrorCode;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
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

    private static final int LIMIT_DAY = 7;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ParticipantRepository participantRepository;
    private final MeetingInfoRepository meetingInfoRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ImageRepository imageRepository;
    private final RoomBlackListRepository blackListRepository;
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
        );
        participantRepository.save(participant);
        participant.updateLastExit(now.get());

        return RoomMapper.toCreateRoomResponse(savedRoom);
    }

    @Transactional
    public ParticipantRoomResponse participateRoom(User user, Long roomId) {

        //참여 여부 확인
        if (participantRepository.existsByUserIdAndRoomId(user.getId(), roomId)) {
            throw new ValidationException(RoomErrorCode.ALREADY_PARTICIPANT);
        }

        Room findRoom = roomRepository.findByIdWithLock(roomId)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));

        //조건 1 . 참여 인원 수
        if (findRoom.getHeadCount() == findRoom.getMaxParticipants()) {
            throw new ValidationException(RoomErrorCode.INVALID_HEADCOUNT);
        }

        //조건 2. 성별
        //방의 성별이 혼성이 아니고 유저 성별과 다르다면
        Gender allowedGender = findRoom.getAllowedGender();
        if (!allowedGender.equals(Gender.UNION) && !allowedGender.equals(user.getGender())) {
            throw new ValidationException(RoomErrorCode.INVALID_GENDER);
        }

        //조건 3. 나이
        int myAge = now.get().getYear() - user.getBirth() + 1;
        if (!(findRoom.getMinAge() <= myAge && findRoom.getMaxAge() >= myAge)) {
            throw new ValidationException(RoomErrorCode.INVALID_AGE);
        }

        //블랙 리스트 여부 확인
        if (blackListRepository.existsByUserIdAndRoomId(user.getId(), roomId)) {
            throw new ValidationException(RoomErrorCode.CAN_NOT_PARTICIPANT);
        }

        //참여 인원 수 증가
        findRoom.increaseHeadCount();

        //참여 정보 저장
        Participant participant = Participant.of(user.getId(), roomId, false);
        participantRepository.save(participant);
        participant.updateLastExit(now.get());

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
    public RoomPageResponse<ChatRoomResponse> findMyChattingRoom(
        User user,
        Pageable pageable
    ) {
        //모임 시간이 (오늘~미래)인 모임
        //ex) 4.11 20:00:48 -> 4.11 00:00:00 변환
        LocalDateTime today = now.get()
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);

        //오늘 이미 참여했거나, 앞으로 참여할 모임
        Slice<Room> myChattingRoom = roomRepository.findMyChattingRoom(user.getId(), today,
            pageable);

        //매핑
        Slice<ChatRoomResponse> myChattingRoomResult = myChattingRoom.map(
            RoomMapper::toChatRoomResponse);

        return RoomMapper.toRoomPageResponse(myChattingRoomResult);
    }

    @Transactional(readOnly = true)
    public RoomPageResponse<GetEndGameResponse> findMyEndGame(
        Long userId,
        Pageable pageable
    ) {
        boolean hasNext = false;

        List<Room> myEndGame = getMyEndGames(userId);

        //3. Slicing
        List<Room> resultList = new ArrayList<>();

        myEndGame.stream()
            .skip(pageable.getOffset())
            .limit(pageable.getPageSize() + 1L)
            .forEach(resultList::add);

        //4.
        if (resultList.size() > pageable.getPageSize()) {
            hasNext = true;
            resultList.remove(resultList.size() - 1);
        }

        // 내가 참여한 종료된 모임의 id 리스트
        List<Long> myEndGameIds = resultList.stream()
            .map(Room::getId)
            .toList();

        // 자신이 참여한 종료된 모임들에 작성한 리뷰들
        List<Review> myEndGameReview = reviewRepository.findReviewsByRoomIdsAndReviewer(
            myEndGameIds, userId);

        //5. slice 변환
        Slice<Room> result = new SliceImpl<>(resultList, pageable, hasNext);

        Slice<GetEndGameResponse> resultMap = result.map(
            room -> RoomMapper.toGetEndGameResponse(room, myEndGameReview));

        return RoomMapper.toRoomPageResponse(resultMap);
    }

    public List<Room> getMyEndGames(Long userId) {
        //1. 내가 참여한 모든 room
        List<Room> myFixGame = roomRepository.findMyFixRoom(userId);

        //2. (모임 확정 day) < 현재 day 인 room
        return new ArrayList<>(
            myFixGame.stream()
                .filter(room -> room.getMeetingInfo().getMeetingTime().toLocalDate()
                    .isBefore(now.get().toLocalDate())
                ).toList()
        );
    }

    @Transactional(readOnly = true)
    public RoomPageResponse<GetAllRoomResponse> findRoomBySearch(
        RoomSearchCondition roomSearchCondition,
        Pageable pageable
    ) {
        Slice<Room> findRooms = roomRepository.findAll(roomSearchCondition, pageable);

        Slice<GetAllRoomResponse> findRoomsMap = findRooms.map(RoomMapper::toGetAllRoomResponse);

        return RoomMapper.toRoomPageResponse(findRoomsMap);
    }

    @Transactional(readOnly = true)
    public RoomInfoResponse findRoomInfo(User user, Long roomId) {
        //1. 모임 정보
        Room findRoom = roomRepository.findById(roomId)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));

        //1-1. 모임 시간 & 장소 정보 추출
        String time = concat(findRoom.getDaySlot().getDescription(),
            findRoom.getTimeSlot().getDescription());
        String startTime = findRoom.getStartTime();
        String subwayLine = findRoom.getSubwayLine();
        String subwayStation = findRoom.getSubwayStation();
        String place = findRoom.getPlaceName();

        //2. 모임 확정 여부 확인
        //모임 확정이라면 -> 모임 확정 시간 장소 정보로 제공
        if (findRoom.getStatus().equals(RoomStatus.FIX)) {
            MeetingInfo meetingInfo = findRoom.getMeetingInfo();
            startTime = meetingInfo.getMeetingTime()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            subwayLine = meetingInfo.getLine();
            subwayStation = meetingInfo.getStation();
            place = meetingInfo.getMeetingPlace();
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

        return RoomMapper.toRoomInfoResponse(findRoom, time, startTime, subwayLine, subwayStation,
            place, isLeader,
            participants);
    }

    @Transactional
    public Room fixRoom(
        User user,
        Long roomId,
        FixRoomRequest request
    ) {
        LocalDateTime today = now.get(); // 오늘
        LocalDateTime fixDay = request.meetingTime(); // 모임 확정하려는 날짜
        //오늘보다 이전 날짜거나 7일 이후를 확정시에 예외
        if (fixDay.isBefore(today) || fixDay.isAfter(today.plusDays(LIMIT_DAY))) {
            throw new ValidationException(INVALID_DATE);
        }

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
            room.getHeadCount(),
            request.line(),
            request.station(),
            request.meetingPlace()
        );

        MeetingInfo savedMeetingInfo = meetingInfoRepository.save(meetingInfo);
        room.fixRoom(savedMeetingInfo);

        return room;
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
    public DeleteRoomFacadeResponse deleteRoom(User user, Long roomId) {
        //방장 여부 확인
        Participant participant = participantRepository.findByUserIdAndRoomId(user.getId(), roomId)
            .orElseThrow(() -> new NotFoundException(INVALID_PARTICIPANT));

        if (!participant.isLeader()) {
            throw new ValidationException(IS_NOT_LEADER);
        }

        //참가자 정보 삭제
        List<ParticipantJpaDto> participants = participantRepository.findParticipantByRoomId(
            roomId);
        participantRepository.deleteParticipantsByRoomId(roomId);

        //채팅 내역 삭제
        chatMessageRepository.deleteByRoomId(roomId);

        //블랙 리스트 내역 삭제
        blackListRepository.deleteByRoomId(roomId);

        //모임 삭제
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM)); // 알림에서 필요한 삭제 전 Room 정보
        roomRepository.deleteById(roomId);

        return new DeleteRoomFacadeResponse(room, participants);
    }

    @Transactional
    public KickOutFacadeResponse kickOutUser(User leader, KickOutUserRequest kickOutUserRequest) {
        //방장 여부 확인
        Long roomId = kickOutUserRequest.roomId();
        Participant leaderInfo = participantRepository.findByUserIdAndRoomId(leader.getId(),
                roomId)
            .orElseThrow(() -> new ValidationException(IS_NOT_LEADER));

        //방장이 아니라면 불가
        if (!leaderInfo.isLeader()) {
            throw new ValidationException(IS_NOT_LEADER);
        }

        //추방자 정보
        Participant kickOutParticipant = participantRepository.findByUserIdAndRoomId(
                kickOutUserRequest.userId(),
                kickOutUserRequest.roomId())
            .orElseThrow(() -> new ValidationException(INVALID_PARTICIPANT));

        User kickOutUser = userRepository.findById(kickOutUserRequest.userId())
            .orElseThrow(() -> new ValidationException(INVALID_PARTICIPANT));

        //추방
        participantRepository.deleteById(kickOutParticipant.getId());

        //블랙리스트 추가
        RoomBlackList blackListUser = RoomBlackList.of(roomId, kickOutUserRequest.userId());
        blackListRepository.save(blackListUser);

        //참가자 수 감소
        Room room = roomRepository.findByIdWithLock(roomId)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));
        room.decreaseHeadCount();

        return new KickOutFacadeResponse(room, kickOutUser.getNickname());
    }

    @Transactional(readOnly = true)
    public List<ParticipantJpaDto> getParticipants(Long roomId) {
        return participantRepository.findParticipantByRoomId(roomId);
    }

}
