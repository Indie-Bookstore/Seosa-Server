package com.seosa.seosa.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;
import java.util.List;

@OpenAPIDefinition(
        info = @Info(title = "Seosa API", version = "v1"),
        servers = {
                @Server(url = "/", description = "Server URL")
        })
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        //Security 스키마 설정
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",bearerAuth))
                .security(Arrays.asList(securityRequirement))
                .paths(filterPaths());
    }

    private Paths filterPaths() {
        Paths paths = new Paths();
        paths.addPathItem("/login", new PathItem()); // 로그인 api
        return paths;
    }

    @Bean
    public GroupedOpenApi apiGroup() {
        String[] paths = {"/**"};

        return GroupedOpenApi.builder()
                .group("service-api-group")
                .pathsToMatch(paths)
                .build();
    }
}