package com.civilwar.boardsignal.user.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.civilwar.boardsignal.common.support.DataJpaTestSupport;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserRepositoryTest extends DataJpaTestSupport {

    @Autowired
    private UserRepository userRepository;

    private User user1;

    private User user2;

    @BeforeEach
    void setUp() {
        user1 = UserFixture.getUserFixture("prprp", "https~");
        user2 = UserFixture.getUserFixture2("provider", "https::");

        userRepository.saveAll(List.of(user1, user2));
    }

    @Test
    @DisplayName("[인자로 들어온 id 리스트에 포함된 유저 id라면 조회 가능하다.]")
    void findAllInIds() {
        List<Long> userIds = List.of(1L, 2L, 3L, 4L, 5L);

        List<User> users = userRepository.findAllInIds(userIds);

        assertAll(
            () -> assertThat(users).hasSize(2),
            () -> assertThat(users.get(0).getId()).isEqualTo(user1.getId()),
            () -> assertThat(users.get(1).getId()).isEqualTo(user2.getId())
        );
    }

    @Test
    @DisplayName("[유저의 signal을 업데이트 할 수 있다.]")
    void updateSignal() {
        User user = userRepository.findById(1L).orElseThrow();
        int EndGameSize = 5;
        userRepository.updateSignal(user.getId(), EndGameSize);

        User findUser = userRepository.findById(1L).orElseThrow();

        assertThat(findUser.getSignal()).isEqualTo(EndGameSize);
    }
}