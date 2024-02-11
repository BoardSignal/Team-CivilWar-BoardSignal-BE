package com.civilwar.boardsignal.user.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.civilwar.boardsignal.common.support.ApiTestSupport;
import com.civilwar.boardsignal.user.dto.request.ApiUserJoinRequest;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@DisplayName("[UserController 테스트]")
class UserControllerTest extends ApiTestSupport {

    @Test
    @DisplayName("[사용자는 회원가입 할 수 있다]")
    void joinUserTest() throws Exception {

        //given
        ApiUserJoinRequest apiUserJoinRequest = new ApiUserJoinRequest(
            "abc1234@gmail.com",
            "최인준",
            "injuning",
            "kakao",
            "providerId",
            List.of("가족게임", "파티게임"),
            "2호선",
            "사당역",
            2000,
            "20~29",
            "male"
        );

        String fileName = "testFile.png";
        MockMultipartFile data = new MockMultipartFile(
            "data",
            null,
            "application/json",
            toJson(apiUserJoinRequest).getBytes(StandardCharsets.UTF_8)
        );
        MockMultipartFile image = new MockMultipartFile(
            "image",
            fileName,
            "image/png",
            new FileInputStream("src/test/resources/" + fileName)
        );

        //then
        mockMvc.perform(
                multipart(HttpMethod.POST, "/api/v1/users")
                    .file(image)
                    .file(data)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.providerId").value(apiUserJoinRequest.providerId()));
    }
}