package com.civilwar.boardsignal.user.presentation;

import com.civilwar.boardsignal.user.application.UserService;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.dto.request.ApiUserModifyRequest;
import com.civilwar.boardsignal.user.dto.request.UserModifyRequest;
import com.civilwar.boardsignal.user.dto.request.ValidNicknameRequest;
import com.civilwar.boardsignal.user.dto.response.UserModifyResponse;
import com.civilwar.boardsignal.user.dto.response.UserProfileResponse;
import com.civilwar.boardsignal.user.dto.response.ValidNicknameResponse;
import com.civilwar.boardsignal.user.mapper.UserApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserApiController {

    private final UserService userService;

    @Operation(summary = "사용자 정보 수정 API")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping
    public ResponseEntity<UserModifyResponse> modifyUser(
        @Valid @RequestPart(value = "data") ApiUserModifyRequest apiUserModifyRequest,
        @RequestPart(value = "image", required = false) MultipartFile image,
        @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        UserModifyRequest userModifyRequest = UserApiMapper.toUserModifyRequest(user.getId(),
            apiUserModifyRequest,
            image);

        UserModifyResponse userModifyResponse = userService.modifyUser(userModifyRequest);

        return ResponseEntity.ok(userModifyResponse);
    }

    @Operation(summary = "프로필 조회 API")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getAnotherUserProfile(
        @Parameter(hidden = true) @AuthenticationPrincipal User loginUser,
        @PathVariable("userId") Long profileUserId
    ) {
        UserProfileResponse userProfileResponse = userService.getUserProfileInfo(profileUserId,
            loginUser);

        return ResponseEntity.ok(userProfileResponse);
    }

    @Operation(summary = "닉네임 중복 체크 API")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/valid")
    public ResponseEntity<ValidNicknameResponse> validNickname(
        @Parameter(hidden = true) @AuthenticationPrincipal User loginUser,
        ValidNicknameRequest validNicknameRequest
    ) {
        ValidNicknameResponse response = userService.isExistNickname(validNicknameRequest,
            loginUser);
        return ResponseEntity.ok(response);
    }

}
