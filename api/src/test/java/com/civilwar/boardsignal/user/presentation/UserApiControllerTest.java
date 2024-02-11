package com.civilwar.boardsignal.user.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.civilwar.boardsignal.common.support.ApiTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@DisplayName("[UserApiController 테스트]")
class UserApiControllerTest extends ApiTestSupport {

    @Test
    @DisplayName("[사용자 정보 조회 API 를 통해 로그인된 사용자의 정보를 조회한다]")
    void getUserProfile_test() throws Exception {
        //when
        ResultActions actions = mockMvc.perform(
            MockMvcRequestBuilders
                .get("/api/v1/users/my")
                .header(AUTHORIZATION, accessToken)
        );

        //then
        actions.andExpectAll(
            status().isOk(),
            jsonPath("$.nickname").value(loginUser.getNickname()),
            jsonPath("$.signal").value(loginUser.getSignal()),
            jsonPath("$.preferCategories.size()").value(loginUser.getCategories().size()),
            jsonPath("$.preferCategories[0]").value(loginUser.getCategories().get(0).getCategory().getDescription()),
            jsonPath("$.preferCategories[1]").value(loginUser.getCategories().get(1).getCategory().getDescription()),
            jsonPath("$.gender").value(loginUser.getGender().getDescription()),
            jsonPath("$.ageGroup").value(loginUser.getAgeGroup().getDescription()),
            jsonPath("$.profileImageUrl").value(loginUser.getProfileImageUrl()),
            jsonPath("$.mannerScore").value(loginUser.getMannerScore())
        );

    }
}