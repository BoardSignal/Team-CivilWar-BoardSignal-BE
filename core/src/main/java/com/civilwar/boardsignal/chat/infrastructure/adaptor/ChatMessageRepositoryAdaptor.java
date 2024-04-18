package com.civilwar.boardsignal.chat.infrastructure.adaptor;

import com.civilwar.boardsignal.chat.domain.entity.ChatMessage;
import com.civilwar.boardsignal.chat.domain.repository.ChatMessageRepository;
import com.civilwar.boardsignal.chat.dto.response.ChatCountDto;
import com.civilwar.boardsignal.chat.dto.response.ChatMessageDto;
import com.civilwar.boardsignal.chat.infrastructure.repository.ChatMessageJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryAdaptor implements ChatMessageRepository {

    private final ChatMessageJpaRepository chatMessageJpaRepository;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageJpaRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> findByRoomId(Long roomId) {
        return chatMessageJpaRepository.findChatMessagesByRoomId(roomId);
    }

    @Override
    public Slice<ChatMessageDto> findChatAllByRoomId(Long roomId, Pageable pageable) {
        return chatMessageJpaRepository.findChatAllByRoomId(roomId, pageable);
    }

    @Override
    public void deleteByRoomId(Long roomId) {
        chatMessageJpaRepository.deleteChatMessagesByRoomId(roomId);
    }

    @Override
    public List<ChatCountDto> countsByRoomIds(Long userId, List<Long> roomIds) {
        return chatMessageJpaRepository.countsByRoomIds(userId, roomIds);
    }
}
