package com.civilwar.boardsignal.chat.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.civilwar.boardsignal.chat.domain.constant.MessageType;
import com.civilwar.boardsignal.chat.domain.entity.ChatMessage;
import com.civilwar.boardsignal.chat.domain.repository.ChatMessageRepository;
import com.civilwar.boardsignal.chat.dto.response.ChatMessageDto;
import com.civilwar.boardsignal.common.support.DataJpaTestSupport;
import com.civilwar.boardsignal.room.RoomFixture;
import com.civilwar.boardsignal.room.domain.entity.Participant;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.room.domain.repository.ParticipantRepository;
import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

class ChatMessageJpaRepositoryTest extends DataJpaTestSupport {

    @Autowired
    ChatMessageRepository chatMessageRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ParticipantRepository participantRepository;

    private Room room;
    private User leaderUser;
    private User user;

    @BeforeEach
    void setup() throws IOException {
        room = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room);

        leaderUser = UserFixture.getUserFixture("providerId", "URL");
        userRepository.save(leaderUser);
        user = UserFixture.getUserFixture2("providerId", "URL");
        userRepository.save(user);

        Participant leaderParticipant = Participant.of(leaderUser.getId(), room.getId(), true);
        participantRepository.save(leaderParticipant);
        Participant participant = Participant.of(user.getId(), room.getId(), false);
        participantRepository.save(participant);

        for (int i = 0; i < 10; i++) {
            Long userId = i % 2 == 0 ? leaderUser.getId() : user.getId();
            ChatMessage chatMessage = ChatMessage.of(room.getId(), userId, "테스트 " + i,
                MessageType.CHAT);
            chatMessageRepository.save(chatMessage);
        }
    }

    @Test
    @DisplayName("[채팅 조회 시, 채팅 메시지와 더불어 페이징 정보가 알맞게 내려온다]")
    void findChatAllByRoomId() {
        //given
        Sort createdAt = Sort.by("createdAt").descending();
        PageRequest pageRequest = PageRequest.of(
            0,
            5,
            createdAt
        );

        //when
        Slice<ChatMessageDto> result = chatMessageRepository.findChatAllByRoomId(
            room.getId(), pageRequest);

        //then
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getSort()).isEqualTo(createdAt);
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("[채팅 조회 시, 채팅 메시지와 더불어 페이징 정보가 알맞게 내려온다2]")
    void findChatAllByRoomId2() {
        //given
        Sort createdAt = Sort.by("createdAt").descending();
        PageRequest pageRequest = PageRequest.of(
            1,
            5,
            createdAt
        );

        //when
        Slice<ChatMessageDto> result = chatMessageRepository.findChatAllByRoomId(
            room.getId(), pageRequest);

        //then
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getSort()).isEqualTo(createdAt);
        assertThat(result.hasNext()).isFalse();
    }
}