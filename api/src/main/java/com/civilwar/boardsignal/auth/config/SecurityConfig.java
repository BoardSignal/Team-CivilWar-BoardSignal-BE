package com.civilwar.boardsignal.auth.config;

import com.civilwar.boardsignal.auth.domain.TokenProvider;
import com.civilwar.boardsignal.auth.filter.CustomAuthenticationFilter;
import com.civilwar.boardsignal.auth.filter.JwtExceptionHandlerFilter;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final JwtExceptionHandlerFilter jwtExceptionHandlerFilter;
    private final UserRepository userRepository;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(
                configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .anonymous(AbstractHttpConfigurer::disable)
            .cors(configurer -> configurer.configurationSource(request -> {
                    CorsConfiguration cors = new CorsConfiguration();
                    cors.setAllowedOrigins(List.of("http://localhost:8080"));
                    cors.setAllowedMethods(Collections.singletonList("*"));
                    cors.setAllowedHeaders(Collections.singletonList("*"));
                    cors.setAllowCredentials(true);
                    return cors;
                }
            ))
            .authorizeHttpRequests(registry -> registry
                //방
                .requestMatchers(HttpMethod.GET, "/api/v1/rooms/my/end-games").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/rooms/**").permitAll()
                //보드게임
                .requestMatchers(HttpMethod.GET, "/api/v1/board-games/**").permitAll()
                //인증
                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/auth/reissue").permitAll()
                .requestMatchers(HttpMethod.GET, "/oauth2/authorization/**").permitAll()
                .anyRequest().authenticated()
            )
            //인증 안 된 사용자 접근 시 예외 처리
            .exceptionHandling(configurer -> configurer
                .authenticationEntryPoint(authenticationEntryPoint))
            //Jwt 관련 예외 처리
            .addFilterBefore(
                jwtExceptionHandlerFilter,
                UsernamePasswordAuthenticationFilter.class
            )
            .addFilterBefore(
                new CustomAuthenticationFilter(tokenProvider, userRepository),
                UsernamePasswordAuthenticationFilter.class)
            .oauth2Login(customizer -> customizer.successHandler(authenticationSuccessHandler))
            .build();
    }
}
