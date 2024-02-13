package com.civilwar.boardsignal.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.common.MultipartFileFixture;
import com.civilwar.boardsignal.image.domain.ImageRepository;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import com.civilwar.boardsignal.user.dto.request.UserJoinRequest;
import com.civilwar.boardsignal.user.dto.response.UserJoinResponse;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("[UserService 테스트]")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ImageRepository imageRepository;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("[사용자는 회원가입 할 수 있다]")
    void joinUserTest() throws IOException {

        //given
        String testUrl = "TEST_URL";
        String providerId = "providerId";

        MockMultipartFile imageFixture = MultipartFileFixture.getMultipartFile();
        UserJoinRequest userJoinRequest = new UserJoinRequest(
            "abc1234@gmail.com",
            "최인준",
            "injuning",
            "kakao",
            providerId,
            List.of(Category.FAMILY, Category.PARTY),
            "2호선",
            "사당역",
            imageFixture,
            2000,
            "20~29",
            "male"
        );
        User userFixture = UserFixture.getUserFixture(providerId, testUrl);

        when(imageRepository.save(imageFixture)).thenReturn(testUrl);
        when(userRepository.save(any(User.class))).thenReturn(userFixture);

        //when
        UserJoinResponse userJoinResponse = userService.joinUser(userJoinRequest);

        //then
        assertThat(userJoinResponse).isNotNull();
    }
}