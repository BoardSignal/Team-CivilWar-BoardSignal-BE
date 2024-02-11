package com.civilwar.boardsignal.user.dto.request;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import java.util.List;

public record UserJoinRequest(

    String email,
    String name,
    String nickName,
    String provider,
    String providerId,
    List<Category> categories,
    String line,
    String station,
    int birth,
    String ageGroup,
    String gender

) {

}
