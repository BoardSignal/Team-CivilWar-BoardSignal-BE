package com.civilwar.boardsignal.auth.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.civilwar.boardsignal.auth.domain.RefreshTokenRepository;
import com.civilwar.boardsignal.support.TestContainerSupport;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
@DisplayName("[RefreshTokenRepository 테스트]")
class RefreshTokenRedisRepositoryTest extends TestContainerSupport {

    Long userId = 1L;
    String uuid = UUID.randomUUID().toString();
    String refreshToken = "refreshToken";
    Long refreshExpiryTime = 30L;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @AfterEach
    public void clean() {
        redisTemplate.delete(userId.toString());
    }

    @Test
    @DisplayName("[RefreshToken을 저장한다]")
    void saveTest() {
        //when
        refreshTokenRepository.save(uuid, refreshToken, refreshExpiryTime);
        String saveRefreshToken = redisTemplate.opsForValue().get(uuid);

        //then
        assertThat(refreshToken).isEqualTo(saveRefreshToken);
    }

    @Test
    @DisplayName("[RefreshToken을 조회한다]")
    void findByIdTest() {
        //given
        refreshTokenRepository.save(uuid, refreshToken, refreshExpiryTime);

        //when
        Optional<String> optional = refreshTokenRepository.findById(uuid);

        //then
        assertThat(optional).contains(refreshToken);
    }

    @Test
    @DisplayName("[RefreshToken을 삭제한다]")
    void deleteTest() {
        //given
        refreshTokenRepository.save(uuid, refreshToken, refreshExpiryTime);

        //when
        Boolean trueResult = refreshTokenRepository.delete(uuid);
        Boolean falseResult = refreshTokenRepository.delete("NotSaveId");

        //then
        assertThat(trueResult).isTrue();
        assertThat(falseResult).isFalse();
    }
}