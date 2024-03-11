package com.civilwar.boardsignal.notification.application;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;

import com.civilwar.boardsignal.notification.domain.entity.Notification;
import com.civilwar.boardsignal.notification.dto.request.NotificationTestRequest;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.entity.UserFcmToken;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {


    private static final String FIREBASE_CONFIG_PATH = "firebase.json";

    private static final String GOOGLE_API_PREFIX = "https://fcm.googleapis.com/v1/projects/";

    private static final String PROJECT_ID = "boardsignal-71515";

    private static final String GOOGLE_API_SUFFIX = "/messages:send";

    private String getAccessToken() throws IOException {
        final GoogleCredentials googleCredentials = GoogleCredentials
            .fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream())
            .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();
        String tokenValue = googleCredentials.getAccessToken().getTokenValue();

        int json = new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream().read();
        log.info("firebase.json : {}", json);


        return tokenValue;
    }

    private HttpEntity<String> getHttpEntity(
        String targetToken,
        String title,
        String body,
        String imageUrl
    ) throws IOException {
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
        log.info("Request Body : {}", requestBody);

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, "Bearer " + getAccessToken());
        headers.add(CONTENT_TYPE, "application/json; charset=utf-8");
        headers.setContentLength(requestBody.length());

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
            log.info("request entity : {}", requestEntity);
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

    //test용(개발단계에서만 있고 제거 예정)
    public String sendMessageTest(NotificationTestRequest notification) throws IOException {
        HttpEntity<String> requestEntity = getHttpEntity(
            notification.targetToken(),
            notification.title(),
            notification.body(),
            notification.imageUrl());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                HttpStatus statusCode = (HttpStatus) response.getStatusCode();
                return statusCode.series() == Series.SERVER_ERROR;
            }
        });

        ResponseEntity<String> response = restTemplate.exchange(
            GOOGLE_API_PREFIX + PROJECT_ID + GOOGLE_API_SUFFIX,
            POST,
            requestEntity,
            String.class
        );




        if(response.getStatusCode() == HttpStatusCode.valueOf(404)){
            return response.toString();
        }

        return response.toString();
    }
}
