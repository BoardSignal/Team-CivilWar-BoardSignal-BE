package com.civilwar.boardsignal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "application, application-api, application-core");
        SpringApplication.run(ApiApplication.class, args);
    }
}
