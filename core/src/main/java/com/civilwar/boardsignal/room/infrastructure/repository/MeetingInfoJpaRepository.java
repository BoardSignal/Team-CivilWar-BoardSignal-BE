package com.civilwar.boardsignal.room.infrastructure.repository;

import com.civilwar.boardsignal.room.domain.entity.MeetingInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingInfoJpaRepository extends JpaRepository<MeetingInfo, Long> {

}
