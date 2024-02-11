package com.civilwar.boardsignal.user.presentation;

import com.civilwar.boardsignal.user.application.UserService;
import com.civilwar.boardsignal.user.dto.request.ApiUserJoinRequest;
import com.civilwar.boardsignal.user.dto.request.UserJoinRequest;
import com.civilwar.boardsignal.user.dto.response.UserJoinResponse;
import com.civilwar.boardsignal.user.mapper.UserApiMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserJoinResponse> joinUser(
        @Valid @RequestPart(value = "data") ApiUserJoinRequest apiUserJoinRequest,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        UserJoinRequest userJoinRequest = UserApiMapper.of(apiUserJoinRequest);

        UserJoinResponse userJoinResponse = userService.joinUser(userJoinRequest, image);

        return ResponseEntity.ok(userJoinResponse);
    }

}
