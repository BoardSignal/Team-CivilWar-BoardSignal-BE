package com.civilwar.boardsignal.user.presentation;

import com.civilwar.boardsignal.user.application.UserService;
import com.civilwar.boardsignal.user.dto.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API")
@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @GetMapping("/api/v1/users/my")
    @ApiResponse(useReturnTypeSchema = true)
    public ResponseEntity<UserProfileResponse> getUserProfile(
        @Parameter(hidden = true)
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserProfileResponse userProfileResponse = userService.getUserProfileInfo(userDetails.getUsername());

        return ResponseEntity.ok(userProfileResponse);
    }
}
