package com.civilwar.boardsignal.user.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.user.dto.request.ApiUserModifyRequest;
import com.civilwar.boardsignal.user.dto.request.UserModifyRequest;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = PRIVATE)
public class UserApiMapper {

    public static UserModifyRequest toUserModifyRequest(Long userId,
        ApiUserModifyRequest apiUserModifyRequest,
        MultipartFile image) {

        List<Category> userCategories = apiUserModifyRequest.categories()
            .stream()
            .map(Category::of)
            .toList();

        return new UserModifyRequest(
            userId,
            apiUserModifyRequest.nickName(),
            userCategories,
            apiUserModifyRequest.gender(),
            apiUserModifyRequest.birth(),
            apiUserModifyRequest.line(),
            apiUserModifyRequest.station(),
            image
        );
    }

}
