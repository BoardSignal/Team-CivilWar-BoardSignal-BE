package com.civilwar.boardsignal.room.domain.repository;

import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;

public interface MeetingInfoRepository {

    MeetingInfo save(MeetingInfo meetingInfo);

    void deleteById(Long id);

}
