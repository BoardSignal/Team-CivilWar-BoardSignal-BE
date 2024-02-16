package com.civilwar.boardsignal.user.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.dto.response.UserJoinResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class UserMapper {

    public static UserJoinResponse toUserJoinResponse(User user) {
        return new UserJoinResponse(user.getId());
    }

}
