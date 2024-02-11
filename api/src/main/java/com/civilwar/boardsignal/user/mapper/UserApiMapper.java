package com.civilwar.boardsignal.user.mapper;

import static lombok.AccessLevel.PRIVATE;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.user.dto.request.ApiUserJoinRequest;
import com.civilwar.boardsignal.user.dto.request.UserJoinRequest;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor(access = PRIVATE)
public class UserApiMapper {

    public static UserJoinRequest toUserJoinRequest(ApiUserJoinRequest apiUserJoinRequest,
        MultipartFile image) {

        List<Category> userCategories = apiUserJoinRequest.categories()
            .stream()
            .map(Category::of)
            .toList();

        return new UserJoinRequest(
            apiUserJoinRequest.email(),
            apiUserJoinRequest.name(),
            apiUserJoinRequest.nickName(),
            apiUserJoinRequest.provider(),
            apiUserJoinRequest.providerId(),
            userCategories,
            apiUserJoinRequest.line(),
            apiUserJoinRequest.station(),
            image,
            apiUserJoinRequest.birth(),
            apiUserJoinRequest.ageGroup(),
            apiUserJoinRequest.gender()
        );
    }

}
