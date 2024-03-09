package com.civilwar.boardsignal.boardgame.infrastructure.adaptor;

import static com.civilwar.boardsignal.boardgame.domain.entity.QBoardGame.boardGame;
import static com.civilwar.boardsignal.boardgame.domain.entity.QBoardGameCategory.boardGameCategory;
import static org.springframework.util.StringUtils.hasText;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.boardgame.domain.constant.Difficulty;
import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.repository.BoardGameQueryRepository;
import com.civilwar.boardsignal.boardgame.dto.request.BoardGameSearchCondition;
import com.civilwar.boardsignal.boardgame.infrastructure.repository.BoardGameJpaRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BoardGameQueryRepositoryAdaptor implements BoardGameQueryRepository {

    private final BoardGameJpaRepository boardGameJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    private BooleanExpression equalDifficulty(String difficulty) {
        if (!hasText(difficulty)) {
            return null;
        }
        return boardGame.difficulty.eq(Difficulty.of(difficulty));
    }

    private BooleanExpression checkRangePlayTime(Integer playTime) {
        if (playTime == null) {
            return null;
        }

        BooleanExpression isLower = boardGame.fromPlayTime.loe(playTime);
        BooleanExpression isGreater = boardGame.toPlayTime.goe(playTime);

        return isLower.and(isGreater);
    }

    private BooleanExpression containsCategory(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return null;
        }

        List<Category> categoryList = categories.stream()
            .map(Category::of)
            .toList();
        return boardGameCategory.category.in(categoryList);
    }

    private BooleanExpression containsKeyword(String searchKeyword) {
        if (!hasText(searchKeyword)) {
            return null;
        }
        return boardGame.title.contains(searchKeyword)
            .or(boardGame.description.contains(searchKeyword));
    }

    @Override
    public Optional<BoardGame> findById(Long id) {
        return boardGameJpaRepository.findById(id);
    }

    @Override
    public Slice<BoardGame> findAll(BoardGameSearchCondition condition, Pageable pageable) {
        boolean hasNext = false;

        List<BoardGame> boardGames = jpaQueryFactory
            .select(boardGame)
            .where(
                equalDifficulty(condition.difficulty()),
                checkRangePlayTime(condition.playTime()),
                containsCategory(condition.categories()),
                containsKeyword(condition.searchKeyword())
            )
            .from(boardGame)
            .join(boardGame.categories, boardGameCategory)
            .groupBy(boardGame)
            .orderBy(boardGame.wishCount.desc())
            .limit(pageable.getPageSize() + 1L)
            .fetch();

        if (boardGames.size() > pageable.getPageSize()) {
            hasNext = true;
            boardGames.remove(boardGames.size() - 1); // 요청한 사이즈보다 하나 더 있는거 확인했으므로 리턴할 때는 마지막꺼 제거
        }
        return new SliceImpl<>(boardGames, pageable, hasNext);
    }

    @Override
    public Optional<BoardGame> findByIdWithLock(Long id) {
        return boardGameJpaRepository.findByIdWithLock(id);
    }

    @Override
    public Slice<BoardGame> findAllInIds(List<Long> ids, Pageable pageable) {
        return boardGameJpaRepository.findAllInIds(ids, pageable);
    }
}
