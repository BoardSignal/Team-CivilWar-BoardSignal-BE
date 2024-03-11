package com.civilwar.boardsignal.room.dto.response;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record CreateRoomRequest(
    String roomTitle,
    String description,
    int minParticipants,
    int maxParticipants,
    String day,
    String time,
    String startTime,
    int minAge,
    int maxAge,
    String subwayLine,
    String subwayStation,
    String place,
    List<String> categories,
    boolean isAllowedOppositeGender,
    MultipartFile image
) {

}
