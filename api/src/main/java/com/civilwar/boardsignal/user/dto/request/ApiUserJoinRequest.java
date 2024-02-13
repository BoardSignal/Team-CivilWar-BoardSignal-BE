package com.civilwar.boardsignal.user.dto.request;

import jakarta.validation.constraints.Email;
import java.util.List;

public record ApiUserJoinRequest(
    @Email
    String email,
    String name,
    String nickName,
    String provider,
    String providerId,
    List<String> categories,
    String line,
    String station,
    int birth,
    String ageGroup,
    String gender
) {

}
