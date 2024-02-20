package com.civilwar.boardsignal.user.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.civilwar.boardsignal.auth.domain.TokenProvider;
import com.civilwar.boardsignal.auth.domain.model.Token;
import com.civilwar.boardsignal.common.support.ApiTestSupport;
import com.civilwar.boardsignal.review.ReviewFixture;
import com.civilwar.boardsignal.review.domain.constant.ReviewContent;
import com.civilwar.boardsignal.review.domain.entity.Review;
import com.civilwar.boardsignal.review.domain.entity.ReviewEvaluation;
import com.civilwar.boardsignal.review.domain.repository.ReviewRepository;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.constants.OAuthProvider;
import com.civilwar.boardsignal.user.domain.constants.Role;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import com.civilwar.boardsignal.user.dto.request.ApiUserModifyRequest;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@DisplayName("[UserController 테스트]")
class UserApiControllerTest extends ApiTestSupport {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("[회원은 정보를 수정 할 수 있다]")
    void joinUserTest() throws Exception {

        //given
        User userFixture = UserFixture.getUserFixture(OAuthProvider.KAKAO.getType(), "testURL");
        userRepository.save(userFixture);

        Long userId = userFixture.getId();
        Token token = tokenProvider.createToken(userId, Role.USER);

        ApiUserModifyRequest apiUserModifyRequest = new ApiUserModifyRequest(
            "injuning",
            "2호선",
            "사당역",
            List.of("가족게임", "파티게임")
        );

        String fileName = "testFile.png";
        MockMultipartFile data = new MockMultipartFile(
            "data",
            null,
            "application/json",
            toJson(apiUserModifyRequest).getBytes(StandardCharsets.UTF_8)
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
                    .header(AUTHORIZATION, "Bearer " + token.accessToken())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(userId));

        User findUser = userRepository.findById(userId)
            .orElseThrow();

        assertThat(findUser.getIsJoined()).isTrue();
        assertThat(findUser.getNickname()).isEqualTo(apiUserModifyRequest.nickName());
        assertThat(findUser.getLine()).isEqualTo(apiUserModifyRequest.line());
        assertThat(findUser.getStation()).isEqualTo(apiUserModifyRequest.station());
    }


    @Test
    @DisplayName("유저의 프로필을 조회할 수 있다.")
    void getUserProfileTest() throws Exception {
        //given
        List<ReviewEvaluation> evaluationFixture = ReviewFixture.getEvaluationFixture();
        Review review = ReviewFixture.getReviewFixture(100L, loginUser.getId(), 1L,
            evaluationFixture);
        reviewRepository.save(review);

        //then
        mockMvc.perform(get("/api/v1/users/my")
                .header(AUTHORIZATION, accessToken))
            .andExpect(jsonPath("$.nickname").value(loginUser.getNickname()))
            .andExpect(jsonPath("$.signal").value(loginUser.getSignal()))
            .andExpect(jsonPath("$.gender").value(loginUser.getGender().getDescription()))
            .andExpect(jsonPath("$.ageGroup").value(loginUser.getAgeGroup().getDescription()))
            .andExpect(jsonPath("$.profileImageUrl").value(loginUser.getProfileImageUrl()))
            .andExpect(jsonPath("$.mannerScore").value(loginUser.getMannerScore()))
            .andExpect(jsonPath("$.reviews[0].content").value(
                ReviewContent.TIME_COMMITMENT.getDescription()))
            .andExpect(jsonPath("$.reviews[0].score").value(
                1))
            .andExpect(jsonPath("$.reviews[1].content").value(
                ReviewContent.GOOD_MANNER.getDescription()))
            .andExpect(jsonPath("$.reviews[1].score").value(
                0))
            .andExpect(jsonPath("$.reviews[2].content").value(
                ReviewContent.FAST_RESPONSE.getDescription()))
            .andExpect(jsonPath("$.reviews[2].score").value(
                0));

    }
}