package com.civilwar.boardsignal.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "BoardSignal API",
        description = "BoardSignal API 명세",
        version = "v1"))
@Configuration
public class SwaggerConfig {

//    @Bean
//    public OpenAPI openAPI() {
//        SecurityScheme securityScheme = new SecurityScheme()
//            .type(SecurityScheme.Type.HTTP)
//            .in(SecurityScheme.In.HEADER).name(AUTHORIZATION);
//        SecurityRequirement securityRequirement = new SecurityRequirement().addList(AUTHORIZATION);
//
//        return new OpenAPI()
//            .components(new Components().addSecuritySchemes(AUTHORIZATION, securityScheme))
//            .security(List.of(securityRequirement));
//    }

    @Bean
    public GroupedOpenApi chatOpenApi() {
        String[] paths = {"/api/v1/**"};

        return GroupedOpenApi.builder()
            .group("API v1")
            .pathsToMatch(paths)
            .build();
    }
}