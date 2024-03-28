package com.civilwar.boardsignal.chat.infrastructure.repository;

import com.civilwar.boardsignal.chat.domain.entity.ChatMessage;
import com.civilwar.boardsignal.chat.dto.response.ChatMessageDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findChatMessagesByRoomId(Long roomId);

    @Query(
        "select new com.civilwar.boardsignal.chat.dto.response.ChatMessageDto("
            + "u.id, u.nickname, u.profileImageUrl, c.content, c.messageType, c.createdAt)"
            + "from ChatMessage as c "
            + "join User as u on c.userId = u.id "
            + "where c.roomId = :roomId "
            + "order by c.createdAt desc ")
    Slice<ChatMessageDto> findChatAllByRoomId(@Param("roomId") Long roomId, Pageable pageable);


    @Modifying(clearAutomatically = true)
    void deleteChatMessagesByRoomId(Long roomId);

}
