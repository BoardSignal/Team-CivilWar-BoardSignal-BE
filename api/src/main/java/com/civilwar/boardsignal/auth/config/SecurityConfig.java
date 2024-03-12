package com.civilwar.boardsignal.auth.config;

import com.civilwar.boardsignal.auth.domain.TokenProvider;
import com.civilwar.boardsignal.auth.filter.CustomAuthenticationFilter;
import com.civilwar.boardsignal.auth.filter.JwtExceptionHandlerFilter;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
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

    private final String[] NEED_AUTHENTICATION = {
        //알림
        "/api/v1/notifications/token",
        //인증
        "/api/v1/auth", "/api/v1/auth/logout",
        //유저
        "/api/v1/users/**",
        //보드게임
        "/api/v1/board-games/like/**", "/api/v1/board-games/tip/**", "/api/v1/board-games/wish/**",
        //리뷰
        "/api/v1/reviews/**",
        //방
        "/api/v1/rooms/end-game/**", "/api/v1/rooms/fix/**", "/api/v1/rooms/unfix/**",
        "/api/v1/rooms/in/**", "/api/v1/rooms/out/**", "/api/v1/rooms/kick", "/api/v1/rooms/my/**"
    };

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
            .authorizeHttpRequests(registry -> registry
                    .requestMatchers(HttpMethod.POST, "/api/v1/rooms/**").authenticated()
                    .requestMatchers(HttpMethod.DELETE, "/api/v1/rooms/**").authenticated()
                    .requestMatchers(NEED_AUTHENTICATION).authenticated()
                    .requestMatchers("/**").permitAll()
//                //알림
//                .requestMatchers("/api/v1/notifications").permitAll()
//                .requestMatchers(
//                    new AntPathRequestMatcher("/api/v1/rooms/my/end-games")).hasRole("USER")
//                //웹소켓
//                .requestMatchers(new AntPathRequestMatcher("/ws/chats"),
//                    new AntPathRequestMatcher("/ws/chats/**")).permitAll()
//                .requestMatchers(HttpMethod.GET,
//                    //방
//                    "/api/v1/rooms/**",
//                    //보드게임
//                    "/api/v1/board-games/**",
//                    //인증
//                    "/api/v1/auth/reissue", "/oauth2/authorization/**",
//                    //스웨거
//                    "/swagger-ui/**",
//                    "/v3/api-docs/**",
//                    "/"
//                ).permitAll()
//                .anyRequest().authenticated()
            )
            //인증 안 된 사용자 접근 시 예외 처리
            .exceptionHandling(configurer -> configurer
                .authenticationEntryPoint(authenticationEntryPoint))
            //Jwt 관련 예외 처리
            .addFilterBefore(
                jwtExceptionHandlerFilter,
                UsernamePasswordAuthenticationFilter.class
            ).addFilterBefore(
                new CustomAuthenticationFilter(tokenProvider, userRepository),
                UsernamePasswordAuthenticationFilter.class)
            .oauth2Login(customizer -> customizer.successHandler(authenticationSuccessHandler))
            .build();
    }
}
