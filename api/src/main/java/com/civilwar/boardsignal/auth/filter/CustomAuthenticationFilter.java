package com.civilwar.boardsignal.auth.filter;

import static com.civilwar.boardsignal.auth.exception.AuthErrorCode.AUTH_TOKEN_MALFORMED;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.civilwar.boardsignal.auth.domain.TokenProvider;
import com.civilwar.boardsignal.auth.domain.model.TokenPayload;
import com.civilwar.boardsignal.common.exception.ValidationException;
import com.civilwar.boardsignal.user.domain.entity.User;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    private Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(token)) {
            String accessToken = token.split(" ")[1];
            TokenPayload payLoad = tokenProvider.getPayLoad(accessToken);

            List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(payLoad.role().getRole())
            );

            User user = userRepository.findById(payLoad.userId())
                .orElseThrow(() -> new ValidationException(AUTH_TOKEN_MALFORMED));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                authorities
            );
            authentication.setDetails(new WebAuthenticationDetails(request));
            return authentication;
        }
        return null;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
