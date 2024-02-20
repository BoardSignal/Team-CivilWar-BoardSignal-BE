package com.civilwar.boardsignal.room.application;

import com.civilwar.boardsignal.image.domain.ImageRepository;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.repository.ParticipantRepository;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.room.dto.mapper.RoomMapper;
import com.civilwar.boardsignal.room.dto.request.CreateRoomResponse;
import com.civilwar.boardsignal.room.dto.response.CreateRoomRequest;
import com.civilwar.boardsignal.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public CreateRoomResponse createRoom(User user, CreateRoomRequest request) {
        String roomImageUrl = imageRepository.save(request.image());

        Room room = RoomMapper.toRoom(roomImageUrl, request);
        Room savedRoom = roomRepository.save(room);

        Participant participant = Participant.of(
            user.getId(),
            savedRoom.getId(),
            request.isAllowedOppositeGender(),
            true // 방 생성자가 방장여부는 true
        ); // 나중에 방 폭파 때 해당 방의 Participant 전부 삭제
        participantRepository.save(participant);

        return RoomMapper.toCreateRoomResponse(savedRoom);
    }

}
