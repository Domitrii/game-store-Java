package com.example.gameplatform.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Game Platform API")
                        .description("""
                                REST API для платформи продажу відеоігор.

                                **Авторизація:** після `/auth/login` скопіюйте токен \
                                і вставте у кнопку Authorize (схема Bearer JWT).
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Game Platform Team")
                                .email("support@gameplatform.ua")))
                .components(new Components()
                        .addSecuritySchemes("Bearer JWT", new SecurityScheme()
                                .name("Bearer JWT")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Вставте JWT-токен, отриманий з /auth/login")));
    }
}
