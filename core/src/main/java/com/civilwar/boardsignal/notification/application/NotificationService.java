package com.civilwar.boardsignal.notification.application;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;

import com.civilwar.boardsignal.notification.domain.entity.Notification;
import com.civilwar.boardsignal.notification.domain.repository.NotificationRepository;
import com.civilwar.boardsignal.notification.dto.request.CreateFcmTokenReequest;
import com.civilwar.boardsignal.notification.dto.request.NotificationTestRequest;
import com.civilwar.boardsignal.notification.dto.response.CreateFcmTokenResponse;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.entity.UserFcmToken;
import com.civilwar.boardsignal.user.domain.repository.UserFcmTokenRepository;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private static final String FIREBASE_CONFIG_PATH = "firebase.json";

    private static final String GOOGLE_API_PREFIX = "https://fcm.googleapis.com/v1/projects";

    private static final String PROJECT_ID = "boardsignal-71515";

    private static final String GOOGLE_API_SUFFIX = "/messages:send";

    private final UserFcmTokenRepository userFcmTokenRepository;
    private final NotificationRepository notificationRepository;

    private String getAccessToken() throws IOException {
        final GoogleCredentials googleCredentials = GoogleCredentials
            .fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream())
            .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        String tokenValue = googleCredentials.getAccessToken().getTokenValue();
        log.info("token : {}", tokenValue);
        return tokenValue;
    }

    private HttpEntity<String> getHttpEntity(
        String targetToken,
        String title,
        String body,
        String imageUrl
    ) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, "Bearer " + getAccessToken());
        headers.add(CONTENT_TYPE, "application/json; charset=utf-8");

        JSONObject content = new JSONObject();
        content.put("title", title);
        content.put("body", body);
        content.put("image", imageUrl);

        JSONObject message = new JSONObject();
        message.put("token", targetToken);
        message.put("notification", content);

        JSONObject result = new JSONObject();
        result.put("message", message);

        String requestBody = result.toString();

        return new HttpEntity<>(requestBody, headers);
    }

    public void sendMessage(Notification notification) {
        User user = notification.getUser();

        //해당 user가 사용하는 기기들의 기기 토큰 모두 조회
        List<String> tokens = user.getUserFcmTokens().stream()
            .map(UserFcmToken::getToken)
            .toList();

        for (String token : tokens) {
            //Request Body 생성
            HttpEntity<String> requestEntity = null;
            try {
                requestEntity = getHttpEntity(
                    token,
                    notification.getTitle(),
                    notification.getBody(),
                    notification.getImageUrl()
                );
            } catch (IOException e) {
                log.error(e.getMessage());
            }

            //Google Api로 알림 전송 요청
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.exchange(
                GOOGLE_API_PREFIX + PROJECT_ID + GOOGLE_API_SUFFIX,
                POST,
                requestEntity,
                String.class
            );
        }

    }

    //사용자 기기 토큰 저장
    public CreateFcmTokenResponse createFcmToken(User user, CreateFcmTokenReequest request) {
        UserFcmToken userFcmToken = UserFcmToken.of(user, request.token());
        UserFcmToken savedToken = userFcmTokenRepository.save(userFcmToken);
        return new CreateFcmTokenResponse(savedToken.getId());
    }

    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    //test용(개발단계에서만 있고 제거 예정)
    public String sendMessageTest(NotificationTestRequest notification) {
        HttpEntity<String> requestEntity = null;

        try {
            requestEntity = getHttpEntity(
                notification.targetToken(),
                notification.title(),
                notification.body(),
                notification.imageUrl()
            );
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
            GOOGLE_API_PREFIX + PROJECT_ID + GOOGLE_API_SUFFIX,
            POST,
            requestEntity,
            String.class
        );

        return response.toString();
    }
}
