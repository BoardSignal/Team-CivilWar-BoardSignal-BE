package com.civilwar.boardsignal.room.dto.request;

public record KickOutUserRequest(
    Long roomId,
    //추방할 참가자 Id
    Long userId
) {

}
