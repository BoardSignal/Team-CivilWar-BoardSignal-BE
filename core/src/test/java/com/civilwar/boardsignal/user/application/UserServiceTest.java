package com.civilwar.boardsignal.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import com.civilwar.boardsignal.common.exception.NotFoundException;
import com.civilwar.boardsignal.fixture.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import com.civilwar.boardsignal.user.dto.response.UserProfileResponse;
import com.civilwar.boardsignal.user.exception.UserErrorCode;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("[UserService 테스트]")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("[providerId 에 해당하는 사용자의 프로필 정보를 조회한다]")
    void getUserProfileInfo_test_1() {
        //given
        User user = UserFixture.getUser();
        given(userRepository.findByProviderId(user.getProviderId()))
            .willReturn(Optional.of(user));

        //when
        UserProfileResponse actual = userService.getUserProfileInfo(user.getProviderId());

        //then
        List<String> expectedCategories = user.getCategories().stream()
            .map(userCategory -> userCategory.getCategory().getDescription())
            .toList();
        assertAll(
            () -> assertThat(actual.nickname()).isEqualTo(user.getNickname()),
            () -> assertThat(actual.signal()).isEqualTo(user.getSignal()),
            () -> assertThat(actual.preferCategories()).containsAll(expectedCategories),
            () -> assertThat(actual.gender()).isEqualTo(user.getGender().getDescription()),
            () -> assertThat(actual.ageGroup()).isEqualTo(user.getAgeGroup().getDescription()),
            () -> assertThat(actual.profileImageUrl()).isEqualTo(user.getProfileImageUrl()),
            () -> assertThat(actual.mannerScore()).isEqualTo(user.getMannerScore())
        );
    }

    @Test
    @DisplayName("[providerId 에 해당하는 사용자가 존재하지 않아 프로필 조회에 실패한다]")
    void getUserProfileInfo_test_2() {
        //given
        User user = UserFixture.getUser();
        given(userRepository.findByProviderId(user.getProviderId()))
            .willReturn(Optional.empty());

        //when
        ThrowingCallable when = () -> userService.getUserProfileInfo(user.getProviderId());

        //then
        assertThatThrownBy(when)
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining(UserErrorCode.NOT_FOUND_BY_PROVIDER_ID.getMessage());
    }
}