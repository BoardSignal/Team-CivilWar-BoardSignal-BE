package com.civilwar.boardsignal.auth.handler;

import com.civilwar.boardsignal.auth.exception.AuthErrorCode;
import com.civilwar.boardsignal.common.exception.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    private String toJson(ErrorResponse response) throws JsonProcessingException {
        return mapper.writeValueAsString(response);
    }

    private void sendJson(HttpServletResponse response, String resultJson) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.setContentLength(resultJson.getBytes().length);
        response.getWriter().write(resultJson);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        AuthErrorCode errorCode = AuthErrorCode.AUTH_REQUIRED;
        log.info("{}", errorCode.getMessage());
        sendJson(response, toJson(new ErrorResponse(errorCode.getMessage(), errorCode.getCode())));
    }
}
