package com.civilwar.boardsignal.user.dto.request;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public record UserModifyRequest(

    Long id,
    String nickName,
    List<Category> categories,
    String gender,
    int birth,
    String line,
    String station,
    MultipartFile image

) {

}
