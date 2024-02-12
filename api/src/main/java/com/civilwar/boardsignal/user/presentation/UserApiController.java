package com.civilwar.boardsignal.user.presentation;

import com.civilwar.boardsignal.user.application.UserService;
import com.civilwar.boardsignal.user.dto.request.ApiUserJoinRequest;
import com.civilwar.boardsignal.user.dto.request.UserJoinRequest;
import com.civilwar.boardsignal.user.dto.response.UserJoinResponse;
import com.civilwar.boardsignal.user.mapper.UserApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Operation(summary = "회원가입 API")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping
    public ResponseEntity<UserJoinResponse> joinUser(
        @Valid @RequestPart(value = "data") ApiUserJoinRequest apiUserJoinRequest,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        UserJoinRequest userJoinRequest = UserApiMapper.toUserJoinRequest(apiUserJoinRequest,
            image);

        UserJoinResponse userJoinResponse = userService.joinUser(userJoinRequest);

        return ResponseEntity.ok(userJoinResponse);
    }

}
