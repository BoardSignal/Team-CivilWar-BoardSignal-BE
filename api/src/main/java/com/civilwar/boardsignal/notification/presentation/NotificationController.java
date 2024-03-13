package com.civilwar.boardsignal.notification.presentation;

import com.civilwar.boardsignal.notification.application.FcmSender;
import com.civilwar.boardsignal.notification.application.NotificationService;
import com.civilwar.boardsignal.notification.dto.request.CreateFcmTokenReequest;
import com.civilwar.boardsignal.notification.dto.request.NotificationTestRequest;
import com.civilwar.boardsignal.notification.dto.response.CreateFcmTokenResponse;
import com.civilwar.boardsignal.notification.dto.response.NotificationPageResponse;
import com.civilwar.boardsignal.notification.dto.response.NotificationResponse;
import com.civilwar.boardsignal.user.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification API")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final FcmSender fcmSender;
    private final NotificationService notificationService;

    //test용
    @PostMapping
    public ResponseEntity<String> notificationTest(@RequestBody NotificationTestRequest request)
        throws IOException {
        String response = fcmSender.sendMessageTest(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "기기 토큰 저장 API")
    @ApiResponse(useReturnTypeSchema = true)
    @PostMapping("/token")
    public ResponseEntity<CreateFcmTokenResponse> createToken(
        @Parameter(hidden = true)
        @AuthenticationPrincipal User user,
        @RequestBody CreateFcmTokenReequest request
    ) {
        CreateFcmTokenResponse response = notificationService.createFcmToken(user, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "나의 알림 목록 조회 API")
    @ApiResponse(useReturnTypeSchema = true)
    @GetMapping("/my")
    public ResponseEntity<NotificationPageResponse<NotificationResponse>> getAllNotifications(
        @Parameter(hidden = true)
        @AuthenticationPrincipal User user,
        Pageable pageable
    ) {
        NotificationPageResponse<NotificationResponse> resposne = notificationService.getAllNotifications(
            user, pageable
        );

        return ResponseEntity.ok(resposne);
    }
}
