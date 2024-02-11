package com.civilwar.boardsignal.user.mapper;

import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.dto.response.UserJoinResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCoreMapper {

    public static UserJoinResponse of(User user) {
        return new UserJoinResponse(user.getProviderId());
    }

}
