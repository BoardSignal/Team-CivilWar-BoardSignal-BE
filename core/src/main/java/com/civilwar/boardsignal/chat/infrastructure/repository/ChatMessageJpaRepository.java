package com.civilwar.boardsignal.chat.infrastructure.repository;

import com.civilwar.boardsignal.chat.domain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessage, Long> {

}
