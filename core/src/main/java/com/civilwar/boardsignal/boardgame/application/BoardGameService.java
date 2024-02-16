package com.civilwar.boardsignal.boardgame.application;

import com.civilwar.boardsignal.boardgame.domain.entity.BoardGame;
import com.civilwar.boardsignal.boardgame.domain.repository.BoardGameQueryRepository;
import com.civilwar.boardsignal.boardgame.dto.BoardGameMapper;
import com.civilwar.boardsignal.boardgame.dto.request.BoardGameSearchCondition;
import com.civilwar.boardsignal.boardgame.dto.response.BoardGamePageResponse;
import com.civilwar.boardsignal.boardgame.dto.response.GetAllBoardGamesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardGameService {

    private final BoardGameQueryRepository boardGameQueryRepository;

    public BoardGamePageResponse<GetAllBoardGamesResponse> getAllBoardGames(
        BoardGameSearchCondition condition, Pageable pageable) {
        Page<BoardGame> boardGames = boardGameQueryRepository.findAll(condition, pageable);
        Page<GetAllBoardGamesResponse> findBoardGames = boardGames.map(
            BoardGameMapper::toGetAllBoardGamesResponse); // 응답 dto 형식으로 변환
        return BoardGameMapper.toBoardGamePageRepsonse(findBoardGames); // 커스텀 페이징 응답 dto에 담음
    }
}
