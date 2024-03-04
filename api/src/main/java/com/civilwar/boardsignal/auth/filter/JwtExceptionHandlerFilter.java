package com.civilwar.boardsignal.auth.filter;

import com.civilwar.boardsignal.common.exception.ErrorResponse;
import com.civilwar.boardsignal.common.exception.ValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class JwtExceptionHandlerFilter extends GenericFilter {

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
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        try {
            chain.doFilter(request, response);
        } catch (ValidationException ve) {
            sendJson(httpServletResponse, toJson(new ErrorResponse(ve.getMessage(), ve.getCode())));
        }

    }
}
