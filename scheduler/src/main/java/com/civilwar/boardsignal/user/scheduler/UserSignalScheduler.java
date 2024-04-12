package com.civilwar.boardsignal.user.scheduler;

import com.civilwar.boardsignal.room.domain.repository.RoomRepository;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
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
    private final RoomRepository roomRepository;
    private final Supplier<LocalDateTime> now;

    @Async(value = "schedulerTask")
    @Scheduled(cron = "0 0 0 * * *")
    public void updateSignal() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            //오늘 날짜
            LocalDateTime today = now.get()
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

            //내가 어제까지 참여한 모임 갯수
            int endRoomCount = roomRepository.countByMyEndRoom(user.getId(), today);

            if (user.getSignal() != endRoomCount) {
                // 종료된 게임이 늘어난 경우에만 업데이트
                userRepository.updateSignal(user.getId(), endRoomCount);
            }
            //조회된 EndGame의 size로 업데이트
        }
    }
}
