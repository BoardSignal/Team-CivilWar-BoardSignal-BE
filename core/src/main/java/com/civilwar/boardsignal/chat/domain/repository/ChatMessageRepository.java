package com.civilwar.boardsignal.chat.domain.repository;

import com.civilwar.boardsignal.chat.domain.entity.ChatMessage;

public interface ChatMessageRepository {

    public ChatMessage save(ChatMessage chatMessage);

}
