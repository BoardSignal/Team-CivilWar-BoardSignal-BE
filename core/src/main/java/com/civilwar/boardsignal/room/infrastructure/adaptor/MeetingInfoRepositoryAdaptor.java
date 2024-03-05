package com.civilwar.boardsignal.room.infrastructure.adaptor;

import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import com.civilwar.boardsignal.room.domain.repository.MeetingInfoRepository;
import com.civilwar.boardsignal.room.infrastructure.repository.MeetingInfoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MeetingInfoRepositoryAdaptor implements MeetingInfoRepository {

    private final MeetingInfoJpaRepository meetingInfoJpaRepository;

    @Override
    public MeetingInfo save(MeetingInfo meetingInfo) {
        return meetingInfoJpaRepository.save(meetingInfo);
    }
}
