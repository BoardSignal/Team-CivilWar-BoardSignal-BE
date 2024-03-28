package com.civilwar.boardsignal.chat.domain.repository;

import com.civilwar.boardsignal.chat.domain.entity.ChatMessage;
import com.civilwar.boardsignal.chat.dto.response.ChatMessageDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatMessageRepository {

    ChatMessage save(ChatMessage chatMessage);

    List<ChatMessage> findByRoomId(Long roomId);

    Slice<ChatMessageDto> findChatAllByRoomId(Long roomId, Pageable pageable);

    void deleteByRoomId(Long roomId);
}
