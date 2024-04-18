package com.civilwar.boardsignal.chat.infrastructure.repository;

import com.civilwar.boardsignal.chat.domain.entity.ChatMessage;
import com.civilwar.boardsignal.chat.dto.response.ChatCountDto;
import com.civilwar.boardsignal.chat.dto.response.ChatMessageDto;
import com.civilwar.boardsignal.chat.dto.response.LastChatMessageDto;
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

    //채팅 메시지 생성시간이 참여자 채팅방 확인 시간 이후인 메시지 갯수 (Participant.lastExit < ChatMessage.createdAt)
    @Query("select new com.civilwar.boardsignal.chat.dto.response.ChatCountDto(c.roomId, count(c)) "
        + "from ChatMessage as c "
        + "join Participant as p "
        + "on p.roomId = c.roomId "
        + "where p.userId = :userId "
        + "and c.roomId in :roomIds "
        + "and c.createdAt>p.lastExit "
        + "group by c.roomId")
    List<ChatCountDto> countsByRoomIds(@Param("userId") Long userId,
        @Param("roomIds") List<Long> roomIds);

    //채팅방 별 마지막 메시지 조회
    @Query("select new com.civilwar.boardsignal.chat.dto.response.LastChatMessageDto(c.roomId, c.content) "
            + "from ChatMessage as c "
            + "where c.roomId in :roomIds "
            + "and c.createdAt in (select max(c.createdAt) from ChatMessage as c group by c.roomId) ")
    List<LastChatMessageDto> findLastChatMessage(@Param("roomIds") List<Long> roomIds);
}
