package com.civilwar.boardsignal.common.support;

import com.civilwar.boardsignal.auth.domain.TokenProvider;
import com.civilwar.boardsignal.auth.domain.model.Token;
import com.civilwar.boardsignal.support.DatabaseCleaner;
import com.civilwar.boardsignal.support.DatabaseCleanerExtension;
import com.civilwar.boardsignal.support.TestContainerSupport;
import com.civilwar.boardsignal.user.UserFixture;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(DatabaseCleaner.class)
@ExtendWith(DatabaseCleanerExtension.class)
public abstract class ApiTestSupport extends TestContainerSupport {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;
    protected User loginUser;
    protected String accessToken;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenProvider tokenProvider;

    protected String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    // API 테스트할 때마다 User 를 저장하고 토큰정보를 가져오지 않기 위해서 하나의 유저와 토큰정보 구성
    @PostConstruct
    public void setUpUser() {
        if (loginUser != null) {
            return;
        }
        User user = userRepository.save(UserFixture.getUserFixture("prpr", "image"));
        Token token = tokenProvider.createToken(user.getId(), user.getRole());
        this.loginUser = user;
        this.accessToken = "Bearer " + token.accessToken();
    }
}
