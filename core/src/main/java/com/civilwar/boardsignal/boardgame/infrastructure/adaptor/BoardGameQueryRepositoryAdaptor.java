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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
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

    @Override
    public Optional<BoardGame> findById(Long id) {
        return boardGameJpaRepository.findById(id);
    }

    @Override
    public Page<BoardGame> findAll(BoardGameSearchCondition condition, Pageable pageable) {
        List<BoardGame> boardGames = jpaQueryFactory
            .select(boardGame)
            .where(
                equalDifficulty(condition.difficulty()),
                checkRangePlayTime(condition.playTime()),
                containsCategory(condition.categories())
            )
            .from(boardGame)
            .join(boardGame.categories, boardGameCategory).fetchJoin()
            .orderBy(boardGame.wishCount.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(boardGames);
    }
}
