package com.civilwar.boardsignal.chat.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

import com.civilwar.boardsignal.chat.domain.constant.MessageType;
import com.civilwar.boardsignal.chat.domain.entity.ChatMessage;
import com.civilwar.boardsignal.chat.domain.repository.ChatMessageRepository;
import com.civilwar.boardsignal.chat.dto.response.ChatCountDto;
import com.civilwar.boardsignal.chat.dto.response.ChatMessageDto;
import com.civilwar.boardsignal.chat.dto.response.LastChatMessageDto;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    @MockBean
    Supplier<LocalDateTime> now;

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

    @Test
    @DisplayName("채팅방 별 유저가 읽지 않은 메시지 갯수를 조회한다.")
    void countsByRoomIdsTest() throws IOException {
        //given
        given(now.get()).willReturn(LocalDateTime.of(2024, 4, 18, 0, 0, 0));

        //2개의 모임 셋팅
        Room room2 = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room2);
        Room room3 = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room3);

        //2개의 모임 참여
        Participant participant2 = Participant.of(leaderUser.getId(), room2.getId(), false);
        participant2.updateLastExit(now.get());
        participantRepository.save(participant2);
        Participant participant3 = Participant.of(leaderUser.getId(), room3.getId(), false);
        participant3.updateLastExit(now.get());
        participantRepository.save(participant3);

        //유저가 채팅방 확인 한 이후, 2번 모임의 1개 채팅 셋팅
        for (int i = 0; i < 1; i++) {
            Long userId = user.getId();
            ChatMessage chatMessage = ChatMessage.of(room2.getId(), userId, "테스트 " + i,
                MessageType.CHAT);
            chatMessageRepository.save(chatMessage);
        }

        //유저가 채팅방 확인 한 이후, 3번 모임의 2개 채팅 셋팅
        for (int i = 0; i < 2; i++) {
            Long userId = user.getId();
            ChatMessage chatMessage = ChatMessage.of(room3.getId(), userId, "테스트 " + i,
                MessageType.CHAT);
            chatMessageRepository.save(chatMessage);
        }

        List<ChatCountDto> chatCountDtos = chatMessageRepository.countsByRoomIds(leaderUser.getId(),
            List.of(room2.getId(),
                room3.getId()));

        assertThat(chatCountDtos.get(0).roomId()).isEqualTo(room2.getId());
        assertThat(chatCountDtos.get(0).uncheckedMessage()).isEqualTo(1);
        assertThat(chatCountDtos.get(1).roomId()).isEqualTo(room3.getId());
        assertThat(chatCountDtos.get(1).uncheckedMessage()).isEqualTo(2);
    }

    @Test
    @DisplayName("채팅방 별 마지막 메시지를 조회한다.")
    void findLastChatMessageTest() throws IOException {
        //given
        given(now.get()).willReturn(LocalDateTime.of(2024, 4, 18, 0, 0, 0));

        //2개의 모임 셋팅
        Room room2 = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room2);
        Room room3 = RoomFixture.getRoom(Gender.UNION);
        roomRepository.save(room3);

        //2번 모임의 2개 채팅 셋팅
        for (int i = 0; i < 2; i++) {
            Long userId = user.getId();
            ChatMessage chatMessage = ChatMessage.of(room2.getId(), userId, "테스트 " + i,
                MessageType.CHAT);
            chatMessageRepository.save(chatMessage);
        }

        //3번 모임의 3개 채팅 셋팅
        for (int i = 0; i < 3; i++) {
            Long userId = user.getId();
            ChatMessage chatMessage = ChatMessage.of(room3.getId(), userId, "테스트 " + i,
                MessageType.CHAT);
            chatMessageRepository.save(chatMessage);
        }

        List<LastChatMessageDto> lastChatMessages = chatMessageRepository.findLastChatMessage(List.of(room2.getId(),
                room3.getId()));

        assertThat(lastChatMessages).hasSize(2);

        assertThat(lastChatMessages.get(0).roomId()).isEqualTo(room2.getId());
        assertThat(lastChatMessages.get(0).content()).isEqualTo("테스트 1");
        assertThat(lastChatMessages.get(1).roomId()).isEqualTo(room3.getId());
        assertThat(lastChatMessages.get(1).content()).isEqualTo("테스트 2");
    }
}