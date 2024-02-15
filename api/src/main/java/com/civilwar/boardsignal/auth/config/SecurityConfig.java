package com.civilwar.boardsignal.auth.config;

import com.civilwar.boardsignal.auth.domain.TokenProvider;
import com.civilwar.boardsignal.auth.filter.CustomAuthenticationFilter;
import com.civilwar.boardsignal.user.domain.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApplicationContext applicationContext;
    private final TokenProvider tokenProvider;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final UserRepository userRepository;

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
                .requestMatchers("/api/v1/board-games").permitAll()
                .requestMatchers("/api/v1/users").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            .exceptionHandling(configurer -> configurer
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
            .addFilterBefore(
                new CustomAuthenticationFilter(tokenProvider, userRepository),
                UsernamePasswordAuthenticationFilter.class)
            .oauth2Login(customizer -> customizer.successHandler(authenticationSuccessHandler))
            .oauth2Client(customizer -> customizer.authorizedClientRepository(
                applicationContext.getBean(OAuth2AuthorizedClientRepository.class)
            ))
            .build();
    }
}
