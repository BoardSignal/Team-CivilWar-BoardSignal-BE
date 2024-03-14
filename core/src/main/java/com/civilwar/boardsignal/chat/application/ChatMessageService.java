package com.civilwar.boardsignal.chat.application;

import com.civilwar.boardsignal.chat.domain.entity.ChatMessage;
import com.civilwar.boardsignal.chat.domain.repository.ChatMessageRepository;
import com.civilwar.boardsignal.chat.dto.request.ChatMessageRequest;
import com.civilwar.boardsignal.chat.dto.response.ChatMessageDto;
import com.civilwar.boardsignal.chat.dto.response.ChatMessageResponse;
import com.civilwar.boardsignal.chat.dto.response.ChatPageResponse;
import com.civilwar.boardsignal.chat.mapper.ChatMessageMapper;
import com.civilwar.boardsignal.common.exception.ValidationException;
import com.civilwar.boardsignal.room.domain.repository.ParticipantRepository;
import com.civilwar.boardsignal.room.exception.RoomErrorCode;
import com.civilwar.boardsignal.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public ChatMessageResponse recordChat(ChatMessageRequest chatMessageRequest) {

        //엔티티 변환
        ChatMessage chatMessage = ChatMessage.of(chatMessageRequest.roomId(),
            chatMessageRequest.userId(),
            chatMessageRequest.content(), chatMessageRequest.type());

        //채팅 로그 기록
        chatMessageRepository.save(chatMessage);

        return ChatMessageMapper.toChatMessageResponse(
            chatMessage);
    }

    @Transactional(readOnly = true)
    public ChatPageResponse<ChatMessageDto> findChatMessages(User user, Long roomId,
        Pageable pageable) {
        //유저가 참여자인지 검증
        boolean isParticipants = participantRepository.existsByUserIdAndRoomId(user.getId(),
            roomId);
        if (!isParticipants) {
            throw new ValidationException(RoomErrorCode.INVALID_PARTICIPANT);
        }

        Slice<ChatMessageDto> chatByRoomId = chatMessageRepository.findChatAllByRoomId(roomId,
            pageable);

        return ChatMessageMapper.toChatPageResponse(chatByRoomId);
    }

}
