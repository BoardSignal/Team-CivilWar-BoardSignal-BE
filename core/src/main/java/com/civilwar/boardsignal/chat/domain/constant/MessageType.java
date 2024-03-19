package com.civilwar.boardsignal.chat.domain.constant;

public enum MessageType {

    //모임 확정
    FIX,
    //모임 취소
    UNFIX,
    //모임 참여
    PARTICIPANT,
    //모임 나가기
    EXIT,
    //일반 채팅
    CHAT,
    //강제 퇴장
    KICK
}
