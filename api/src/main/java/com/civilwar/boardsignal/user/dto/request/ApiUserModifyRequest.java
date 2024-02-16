package com.civilwar.boardsignal.user.dto.request;

import java.util.List;

public record ApiUserModifyRequest(
    String nickName,
    String line,
    String station,
    List<String> categories
) {

}
