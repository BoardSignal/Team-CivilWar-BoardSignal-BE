package com.civilwar.boardsignal.chat.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.civilwar.boardsignal.chat.domain.constant.MessageType;
import com.civilwar.boardsignal.chat.domain.entity.ChatMessage;
import com.civilwar.boardsignal.chat.domain.repository.ChatMessageRepository;
import com.civilwar.boardsignal.common.exception.ValidationException;
import com.civilwar.boardsignal.common.support.ApiTestSupport;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class ChatMessageControllerTest extends ApiTestSupport {

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
    @Disabled
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
    @Disabled
    @DisplayName("[참여하지 않은 사용자가 채팅 내역 조회 시도 시, 예외가 발생한다]")
    void findChatMessagesTest() throws Exception {
        //given
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "0");
        params.add("size", "10");

        //then
        mockMvc.perform(get("/api/v1/rooms/chats/" + room.getId())
                .header(AUTHORIZATION, accessToken)
                .params(params))
            .andExpect(
                result -> assertThat(result.getResolvedException()).getClass().isAssignableFrom(
                    ValidationException.class))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @Disabled
    @DisplayName("[모임에 참가한 사용자는 채팅 내역을 조회할 수 있다]")
    void findChatMessageTest2() throws Exception {
        //given
        Participant anotherLoginUser = Participant.of(loginUser.getId(), room.getId(), false);
        participantRepository.save(anotherLoginUser);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "0");
        params.add("size", "11");

        //then
        mockMvc.perform(get("/api/v1/rooms/chats/" + room.getId())
                .header(AUTHORIZATION, accessToken)
                .params(params))
            .andExpect(jsonPath("$.chatList.size()").value(10))
            .andExpect(jsonPath("$.currentPageNumber").value(0))
            .andExpect(jsonPath("$.size").value(11))
            .andExpect(jsonPath("$.hasNext").value(false));
    }
}