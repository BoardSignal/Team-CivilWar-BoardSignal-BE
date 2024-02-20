package com.civilwar.boardsignal.boardgame.domain.repository;

import static com.civilwar.boardsignal.boardgame.domain.constant.Category.FAMILY;
import static com.civilwar.boardsignal.boardgame.domain.constant.Category.WAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGameCategory;
import com.civilwar.boardsignal.boardgame.domain.entity.Tip;
import com.civilwar.boardsignal.common.support.DataJpaTestSupport;
import com.civilwar.boardsignal.fixture.BoardGameFixture;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("[TipRepository 테스트]")
@Slf4j
class TipRepositoryTest extends DataJpaTestSupport {

    @Autowired
    private TipRepository tipRepository;

    @Autowired
    private BoardGameRepository boardGameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardGameQueryRepository boardGameQueryRepository;

    @Test
    @DisplayName("[특정 보드게임의 공략들을 전부 조회할 수 있다.]")
    void findAllWithBoardGameId() {
        //given
        BoardGameCategory warGame = BoardGameFixture.getBoardGameCategory(WAR);
        BoardGameCategory familyGame = BoardGameFixture.getBoardGameCategory(FAMILY);
        BoardGame boardGame = BoardGameFixture.getBoardGame(List.of(warGame, familyGame));
        BoardGame savedGame = boardGameRepository.save(boardGame);

        Tip tip = BoardGameFixture.getTip(1L, savedGame.getId(), "꿀팁");
        Tip tip2 = BoardGameFixture.getTip(2L, savedGame.getId(), "꿀팁2");

        tipRepository.saveAll(List.of(tip, tip2)); //한 보드게임에 대해 공략 두개 등록

        //when
        List<Tip> tips = tipRepository.findAllByBoardGameId(savedGame.getId());

        //then
        assertAll(
            () -> assertThat(tips).hasSize(2),
            () -> assertThat(tips.get(0).getContent()).isEqualTo(tip.getContent()),
            () -> assertThat(tips.get(0).getBoardGameId()).isEqualTo(savedGame.getId()),
            () -> assertThat(tips.get(1).getContent()).isEqualTo(tip2.getContent()),
            () -> assertThat(tips.get(1).getBoardGameId()).isEqualTo(savedGame.getId())
        );
    }
}