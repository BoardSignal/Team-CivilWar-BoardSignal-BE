package com.civilwar.boardsignal.boardgame.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGameCategory;
import com.civilwar.boardsignal.boardgame.domain.repository.BoardGameQueryRepository;
import com.civilwar.boardsignal.boardgame.domain.repository.BoardGameRepository;
import com.civilwar.boardsignal.fixture.BoardGameFixture;
import com.civilwar.boardsignal.support.TestContainerSupport;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class BoardGameConcurrencyTest extends TestContainerSupport {

    @Autowired
    private BoardGameService boardGameService;

    @Autowired
    private BoardGameRepository boardGameRepository;

    @Autowired
    private BoardGameQueryRepository boardGameQueryRepository;

    @Test
    @DisplayName("[동시에 50의 사용자가 한 게임에 찜 등록을 하면 50개의 찜등록이 된다.]")
    void addTipWithConcurrency() throws InterruptedException {
        int threadCount = 50;

        BoardGameCategory warGame = BoardGameFixture.getBoardGameCategory(Category.WAR);
        BoardGame boardGame = BoardGameFixture.getBoardGame(List.of(warGame));
        BoardGame savedGame = boardGameRepository.save(boardGame);

        List<User> users = new ArrayList<>();
        for (int i = 1; i <= threadCount; i++) {
            User user = UserFixture.getUserFixture(String.valueOf(i), "httips~~");
            ReflectionTestUtils.setField(user, "id", (long) i);
            users.add(user);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            User user = users.get(i % users.size());

            executorService.submit(() -> {
                try {
                    boardGameService.wishBoardGame(user, savedGame.getId());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        BoardGame findGame = boardGameQueryRepository.findById(1L).orElseThrow();

        assertThat(findGame.getWishCount()).isEqualTo(threadCount);
    }

}
