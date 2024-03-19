package com.civilwar.boardsignal.room.presentation;

import com.civilwar.boardsignal.room.dto.mapper.RoomApiMapper;
import com.civilwar.boardsignal.room.dto.request.ApiCreateRoomRequest;
import com.civilwar.boardsignal.room.dto.request.ApiFixRoomRequest;
import com.civilwar.boardsignal.room.dto.request.CreateRoomRequest;
import com.civilwar.boardsignal.room.dto.request.FixRoomRequest;
import com.civilwar.boardsignal.room.dto.request.KickOutUserRequest;
import com.civilwar.boardsignal.room.dto.request.RoomSearchCondition;
import com.civilwar.boardsignal.room.dto.response.ChatRoomResponse;
import com.civilwar.boardsignal.room.dto.response.CreateRoomResponse;
import com.civilwar.boardsignal.room.dto.response.ExitRoomResponse;
import com.civilwar.boardsignal.room.dto.response.FixRoomResponse;
import com.civilwar.boardsignal.room.dto.response.GetAllRoomResponse;
import com.civilwar.boardsignal.room.dto.response.GetEndGameResponse;
import com.civilwar.boardsignal.room.dto.response.GetEndGameUsersResponse;
import com.civilwar.boardsignal.room.dto.response.KickOutResponse;
import com.civilwar.boardsignal.room.dto.response.ParticipantRoomResponse;
import com.civilwar.boardsignal.room.dto.response.RoomInfoResponse;
import com.civilwar.boardsignal.room.dto.response.RoomPageResponse;
import com.civilwar.boardsignal.room.facade.RoomFacade;
import com.civilwar.boardsignal.user.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Room API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomFacade roomFacade;

    @Operation(summary = "방 생성 API")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @RequestPart(value = "image", required = false) MultipartFile image,
        @Valid @RequestPart(value = "data") ApiCreateRoomRequest request
    ) {
        CreateRoomRequest createRoomRequest = RoomApiMapper.toCreateRoomRequest(image, request);

        CreateRoomResponse createRoomResponse = roomFacade.createRoom(user, createRoomRequest);

        return ResponseEntity.ok(createRoomResponse);
    }

    @Operation(summary = "방 참여 API")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/in/{roomId}")
    public ResponseEntity<ParticipantRoomResponse> participantRoom(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable("roomId") Long roomId
    ) {
        ParticipantRoomResponse participantRoomResponse = roomFacade.participateRoom(user,
            roomId);

        return ResponseEntity.ok(participantRoomResponse);
    }

    @Operation(summary = "방 나가기 API")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/out/{roomId}")
    public ResponseEntity<ExitRoomResponse> exitRoom(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable("roomId") Long roomId
    ) {
        ExitRoomResponse exitRoomResponse = roomFacade.exitRoom(user, roomId);

        return ResponseEntity.ok(exitRoomResponse);
    }

    @Operation(summary = "내가 현재 참여하고 있는 모임 조회 API (채팅 목록용)")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/my/games")
    public ResponseEntity<RoomPageResponse<ChatRoomResponse>> getMyGame(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        Pageable pageable
    ) {
        RoomPageResponse<ChatRoomResponse> myGame = roomFacade.findMyGame(user, pageable);

        return ResponseEntity.ok(myGame);
    }

    @Operation(summary = "내가 이전에 참여한 모임 조회 API")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/my/end-games")
    public ResponseEntity<RoomPageResponse<GetEndGameResponse>> getMyEndGame(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        Pageable pageable
    ) {

        RoomPageResponse<GetEndGameResponse> myParticipants = roomFacade.findMyEndGame(
            user.getId(), pageable);

        return ResponseEntity.ok(myParticipants);
    }

    @Operation(summary = "방 필터링 조회 API")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/filter")
    public ResponseEntity<RoomPageResponse<GetAllRoomResponse>> getSearchRoom(
        RoomSearchCondition condition,
        Pageable pageable
    ) {
        RoomPageResponse<GetAllRoomResponse> rooms = roomFacade.findRoomBySearch(condition,
            pageable);
        return ResponseEntity.ok(rooms);
    }

    @Operation(summary = "방 상세정보 조회 API")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomInfoResponse> getRoomInfo(
        @PathVariable("roomId") Long roomId,
        @AuthenticationPrincipal User user
    ) {
        RoomInfoResponse roomInfo = roomFacade.findRoomInfo(user, roomId);
        return ResponseEntity.ok(roomInfo);
    }

    @Operation(summary = "모임 확정 API(방장용)")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/fix/{roomId}")
    public ResponseEntity<FixRoomResponse> fixRoom(
        @Parameter(hidden = true)
        @AuthenticationPrincipal User user,
        @PathVariable("roomId") Long roomId,
        @RequestBody ApiFixRoomRequest request
    ) {
        FixRoomRequest fixRoomRequest = RoomApiMapper.toFixRoomRequest(request);
        FixRoomResponse response = roomFacade.fixRoom(user, roomId, fixRoomRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "종료된 게임에 참여한 유저 조회 API")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/end-game/{roomId}")
    public ResponseEntity<GetEndGameUsersResponse> getEndGameUsers(
        @Parameter(hidden = true)
        @AuthenticationPrincipal User user,
        @PathVariable("roomId") Long roomId
    ) {
        GetEndGameUsersResponse response = roomFacade.getEndGameUsersResponse(user, roomId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모임 확정 취소 API")
    @ApiResponse(useReturnTypeSchema = true)
    @DeleteMapping("/unfix/{roomId}")
    public void unFixRoom(
        @Parameter(hidden = true)
        @AuthenticationPrincipal User user,
        @PathVariable("roomId") Long roomId
    ) {
        roomFacade.unFixRoom(user, roomId);
    }

    @Operation(summary = "모임 삭제 API (방장용)")
    @ApiResponse(useReturnTypeSchema = true)
    @DeleteMapping("/{roomId}")
    public void deleteRoom(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable("roomId") Long roomId
    ) {
        roomFacade.deleteRoom(user, roomId);
    }


    @Operation(summary = "참가자 추방 API (방장용)")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/kick")
    public ResponseEntity<KickOutResponse> kickOut(
        @Parameter(hidden = true)
        @AuthenticationPrincipal User user,
        @RequestBody KickOutUserRequest kickOutUserRequest
    ) {
        KickOutResponse kickOutResponse = roomFacade.kickOutUser(user, kickOutUserRequest);

        return ResponseEntity.ok(kickOutResponse);
    }
}
