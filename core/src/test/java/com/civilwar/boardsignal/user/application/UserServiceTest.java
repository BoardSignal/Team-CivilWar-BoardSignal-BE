package com.civilwar.boardsignal.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.civilwar.boardsignal.boardgame.domain.constant.Category;
import com.civilwar.boardsignal.common.MultipartFileFixture;
import com.civilwar.boardsignal.image.domain.ImageRepository;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.constants.Gender;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import com.civilwar.boardsignal.user.dto.request.UserModifyRequest;
import com.civilwar.boardsignal.user.dto.response.UserModifyResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("[UserService 테스트]")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private Supplier<LocalDateTime> now;
    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("[회원은 정보를 수정 할 수 있다]")
    void joinUserTest() throws IOException {

        //given
        Long id = 1L;
        String testUrl = "TEST_URL";
        String providerId = "providerId";

        MockMultipartFile imageFixture = MultipartFileFixture.getMultipartFile();
        UserModifyRequest userModifyRequest = new UserModifyRequest(
            id,
            "injuning",
            List.of(Category.FAMILY, Category.PARTY),
            Gender.MALE.getDescription(),
            2000,
            "2호선",
            "사당역",
            imageFixture
        );
        User userFixture = UserFixture.getUserFixture(providerId, testUrl);
        Boolean isJoined = userFixture.getIsJoined();

        when(now.get()).thenReturn(LocalDateTime.of(2024,11,20,0,0,0));
        when(imageRepository.save(imageFixture)).thenReturn(testUrl);
        when(userRepository.findById(id)).thenReturn(Optional.of(userFixture));
        ReflectionTestUtils.setField(userFixture, "id", id);

        //when
        UserModifyResponse userModifyResponse = userService.modifyUser(userModifyRequest);

        //then
        assertThat(userModifyResponse.id()).isEqualTo(id);
        assertThat(userFixture.getIsJoined()).isNotEqualTo(isJoined);
    }
}