package com.civilwar.boardsignal.user.dto.request;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record UserJoinRequest(

    String email,
    String name,
    String nickName,
    String provider,
    String providerId,
    List<Category> categories,
    String line,
    String station,
    MultipartFile image,
    int birth,
    String ageGroup,
    String gender

) {

}
