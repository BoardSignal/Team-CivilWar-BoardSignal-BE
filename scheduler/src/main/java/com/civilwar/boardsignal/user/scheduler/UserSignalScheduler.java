package com.civilwar.boardsignal.user.scheduler;

import com.civilwar.boardsignal.room.application.RoomService;
import com.civilwar.boardsignal.room.domain.entity.Room;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSignalScheduler {

    private final UserRepository userRepository;
    private final RoomService roomService;

    @Async(value = "schedulerTask")
    @Scheduled(cron = "0 0 0 * * *")
    public void updateSignal(){
        List<User> users = userRepository.findAll();

        for(User user : users) {
            List<Room> myEndGames = roomService.getMyEndGames(user.getId());
            if(user.getSignal() != myEndGames.size()){
                // 종료된 게임이 늘어난 경우에만 업데이트
                userRepository.updateSignal(user.getId(), myEndGames.size());
            }
            //조회된 EndGame의 size로 업데이트
        }
    }
}
