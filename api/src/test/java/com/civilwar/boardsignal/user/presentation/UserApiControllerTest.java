package com.civilwar.boardsignal.user.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.civilwar.boardsignal.auth.domain.TokenProvider;
import com.civilwar.boardsignal.auth.domain.model.Token;
import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.boardgame.domain.entity.Wish;
import com.civilwar.boardsignal.boardgame.domain.repository.WishRepository;
import com.civilwar.boardsignal.common.support.ApiTestSupport;
import com.civilwar.boardsignal.review.ReviewFixture;
import com.civilwar.boardsignal.review.domain.constant.ReviewContent;
import com.civilwar.boardsignal.review.domain.entity.Review;
import com.civilwar.boardsignal.review.domain.entity.ReviewEvaluation;
import com.civilwar.boardsignal.review.domain.repository.ReviewRepository;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.constants.AgeGroup;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.constants.OAuthProvider;
import com.civilwar.boardsignal.user.domain.constants.Role;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import com.civilwar.boardsignal.user.dto.request.ApiUserModifyRequest;
import com.civilwar.boardsignal.user.dto.request.ValidNicknameRequest;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    @Autowired
    private WishRepository wishRepository;
    @MockBean
    private Supplier<LocalDateTime> now;

    @Test
    @DisplayName("[회원은 정보를 수정 할 수 있다]")
    void joinUserTest() throws Exception {

        //given
        given(now.get()).willReturn(LocalDateTime.of(2024, 4, 16, 0, 0, 0));

        User userFixture = UserFixture.getUserFixture(OAuthProvider.KAKAO.getType(), "testURL");
        userRepository.save(userFixture);

        Long userId = userFixture.getId();
        Token token = tokenProvider.createToken(userId, Role.USER);

        ApiUserModifyRequest apiUserModifyRequest = new ApiUserModifyRequest(
            "injuning",
            Gender.MALE.getDescription(),
            1970,
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
        assertThat(findUser.getAgeGroup()).isEqualTo(AgeGroup.FIFTY);
    }


    @Test
    @DisplayName("유저의 자신의 프로필을 조회할 수 있다.")
    void getUserProfileTest() throws Exception {
        //given
        List<ReviewEvaluation> evaluationFixture = ReviewFixture.getEvaluationFixture();
        Review review = ReviewFixture.getReviewFixture(100L, loginUser.getId(), 1L,
            evaluationFixture);
        reviewRepository.save(review);

        Wish wish1 = Wish.of(loginUser.getId(), 1L);
        Wish wish2 = Wish.of(loginUser.getId(), 2L);
        wishRepository.save(wish1);
        wishRepository.save(wish2);

        //then
        mockMvc.perform(get("/api/v1/users/" + loginUser.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(jsonPath("$.nickname").value(loginUser.getNickname()))
            .andExpect(jsonPath("$.signal").value(loginUser.getSignal()))
            .andExpect(jsonPath("$.gender").value(loginUser.getGender().getDescription()))
            .andExpect(jsonPath("$.ageGroup").value(loginUser.getAgeGroup().getDescription()))
            .andExpect(jsonPath("$.profileImageUrl").value(loginUser.getProfileImageUrl()))
            .andExpect(jsonPath("$.signalTemperature").value(loginUser.getMannerScore()))
            .andExpect(jsonPath("$.wishCount").value(2))
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
                0))
            .andExpect(jsonPath("$.isProfileManager").value(true));
    }

    @Test
    @DisplayName("타인의 프로필을 조회할 수 있다.")
    void getUserProfileTest2() throws Exception {
        //given
        User anotherUser = UserFixture.getUserFixture2("providerId", "testUrl");
        userRepository.save(anotherUser);
        List<ReviewEvaluation> evaluationFixture = ReviewFixture.getEvaluationFixture();
        Review review = ReviewFixture.getReviewFixture(100L, anotherUser.getId(), 1L,
            evaluationFixture);
        reviewRepository.save(review);

        Wish wish1 = Wish.of(anotherUser.getId(), 1L);
        Wish wish2 = Wish.of(anotherUser.getId(), 2L);
        wishRepository.save(wish1);
        wishRepository.save(wish2);

        //then
        mockMvc.perform(get("/api/v1/users/" + anotherUser.getId())
                .header(AUTHORIZATION, accessToken))
            .andExpect(jsonPath("$.nickname").value(anotherUser.getNickname()))
            .andExpect(jsonPath("$.signal").value(anotherUser.getSignal()))
            .andExpect(jsonPath("$.gender").value(anotherUser.getGender().getDescription()))
            .andExpect(jsonPath("$.ageGroup").value(anotherUser.getAgeGroup().getDescription()))
            .andExpect(jsonPath("$.profileImageUrl").value(anotherUser.getProfileImageUrl()))
            .andExpect(jsonPath("$.signalTemperature").value(anotherUser.getMannerScore()))
            .andExpect(jsonPath("$.wishCount").value(2))
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
                0))
            .andExpect(jsonPath("$.isProfileManager").value(false));
    }

    @Test
    @DisplayName("[중복X 닉네임이라면, true 반환]")
    void validNicknameTest1() throws Exception {
        //given
        String notExistName = "절대 중복된 이름 아님";
        ValidNicknameRequest validNicknameRequest = new ValidNicknameRequest(notExistName);

        //then
        mockMvc.perform(post("/api/v1/users/valid")
                .header(AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(validNicknameRequest)))
            .andExpect(jsonPath("$.isNotValid").value(true));
    }

    @Test
    @DisplayName("[중복O 닉네임이지만, 그 닉네임이 해당 사용자의 닉네임이라면 true 반환]")
    void validNicknameTest2() throws Exception {
        //given
        String existName = loginUser.getName();
        loginUser.updateUser(existName, List.of(Category.CUSTOMIZABLE), Gender.MALE, 2000,
            AgeGroup.TWENTY, "2호선", "사당역", "testURL");
        userRepository.save(loginUser);
        ValidNicknameRequest validNicknameRequest = new ValidNicknameRequest(existName);

        //then
        mockMvc.perform(post("/api/v1/users/valid")
                .header(AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(validNicknameRequest)))
            .andExpect(jsonPath("$.isNotValid").value(true));
    }

    @Test
    @DisplayName("[중복O 닉네임이지만, 기존 닉네임을 가진 사용자의 회원가입 여부가 false 라면, true 반환]")
    void validNicknameTest3() throws Exception {
        //given
        User anotherUser = UserFixture.getUserFixture("providerId", "testURL");
        userRepository.save(anotherUser);

        String existName = anotherUser.getName();

        ValidNicknameRequest validNicknameRequest = new ValidNicknameRequest(existName);

        //then
        mockMvc.perform(post("/api/v1/users/valid")
                .header(AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(validNicknameRequest)))
            .andExpect(jsonPath("$.isNotValid").value(true));
    }

    @Test
    @DisplayName("[중복O 닉네임이라면, false 반환]")
    void validNicknameTest4() throws Exception {
        //given
        User anotherUser = UserFixture.getUserFixture("providerId", "testURL");
        String existName = anotherUser.getName();
        userRepository.save(anotherUser);
        anotherUser.updateUser(existName, List.of(Category.CUSTOMIZABLE), Gender.MALE, 2000,
            AgeGroup.TWENTY, "2호선", "사당역", "testURL");
        userRepository.save(loginUser);

        ValidNicknameRequest validNicknameRequest = new ValidNicknameRequest(existName);

        //then
        mockMvc.perform(post("/api/v1/users/valid")
                .header(AUTHORIZATION, accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(validNicknameRequest)))
            .andExpect(jsonPath("$.isNotValid").value(true));
    }
}
