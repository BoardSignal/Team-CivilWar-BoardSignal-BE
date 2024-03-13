package com.civilwar.boardsignal.chat.domain.entity;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;

import com.civilwar.boardsignal.chat.domain.constant.MessageType;
import com.civilwar.boardsignal.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "CHAT_MESSAGE_TABLE")
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHAT_MESSAGE_ID")
    private Long id;

    @Column(name = "CHAT_MESSAGE_ROOM_ID")
    private Long roomId;

    @Column(name = "CHAT_MESSAGE_USER_ID")
    private Long userId;

    @Column(name = "CHAT_MESSAGE_CONTENT")
    private String content;

    @Column(name = "CHAT_MESSAGE_TYPE")
    @Enumerated(STRING)
    private MessageType messageType;

    @Builder(access = PRIVATE)
    public ChatMessage(
        @NonNull Long roomId,
        @NonNull Long userId,
        @NonNull String content,
        @NonNull MessageType messageType
    ) {
        this.roomId = roomId;
        this.userId = userId;
        this.content = content;
        this.messageType = messageType;
    }

    public static ChatMessage of(
        Long roomId,
        Long userId,
        String content,
        MessageType messageType
    ) {
        return ChatMessage.builder()
            .roomId(roomId)
            .userId(userId)
            .content(content)
            .messageType(messageType)
            .build();
    }
}
