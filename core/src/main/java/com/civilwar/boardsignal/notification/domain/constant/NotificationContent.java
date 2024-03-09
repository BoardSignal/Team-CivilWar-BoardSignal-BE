package com.civilwar.boardsignal.notification.domain.constant;

import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NotificationContent {
    ROOM_CREATED_NEARLY("지역매칭",
        input -> String.format("%s에서 모임이 생성됐습니다. 확인해보세요!", input)),
    KICKED_FROM_ROOM("강퇴",
        input -> String.format("%s 방에서 강제 퇴장 되었습니다.", input)),
    ROOM_FIXED("매칭 확정",
        input -> String.format("%s 방의 매칭이 확정되었습니다.", input)),
    ROOM_REMOVED("방 삭제",
        input -> String.format("%s 방이 삭제되었습니다", input)),
    REVIEW_RECIEVED("리뷰",
        input -> String.format("%s 님이 리뷰를 남겼습니다.", input)),
    REVIEW_REQUIRED("리뷰",
        input -> String.format("%s 님이 작성해야 할 리뷰가 있어요.👀", input));


    private final String title;
    private final Function<String, String> expression;

    public String getMessage(String input) {
        return expression.apply(input);
    }

}
