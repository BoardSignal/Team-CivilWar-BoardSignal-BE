package com.civilwar.boardsignal.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
@Profile(value = "dev")
@Slf4j
public class FcmConfig {

    private static final String FIREBASE_CONFIG_PATH = "firebase.json";

    @PostConstruct
    public void initialize() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream());
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            log.error("FCM error message : " + e.getMessage());
        }

    }
}
