package com.civilwar.boardsignal.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperty {

    private String clientSecret;
    private long accessExpiryTime;
    private long refreshExpiryTime;
}
