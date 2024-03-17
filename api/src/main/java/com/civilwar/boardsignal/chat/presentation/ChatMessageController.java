package com.civilwar.boardsignal.chat.presentation;

import com.civilwar.boardsignal.chat.application.ChatMessageService;
import com.civilwar.boardsignal.chat.dto.response.ChatPageResponse;
import com.civilwar.boardsignal.chat.dto.response.GetChatMessageResponse;
import com.civilwar.boardsignal.user.domain.entity.User;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/api/v1/rooms/chats/{roomId}")
    public ResponseEntity<ChatPageResponse<GetChatMessageResponse>> findChatMessages(
        @Parameter(hidden = true) @AuthenticationPrincipal User user,
        @PathVariable("roomId") Long roomId,
        Pageable pageable
    ) {
        ChatPageResponse<GetChatMessageResponse> chatMessages = chatMessageService.findChatMessages(user,
            roomId, pageable);
        return ResponseEntity.ok(chatMessages);
    }

}
