package com.tastyhouse.webapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 설정
 *
 * API 문서: http://localhost:8080/swagger-ui.html
 * OpenAPI JSON: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI webApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TastyHouse Web API")
                        .description("TastyHouse 클라이언트용 공개 API 문서")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("TastyHouse Team")
                                .email("support@tastyhouse.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("로컬 개발 서버"),
                        new Server()
                                .url("https://api.tastyhouse.com")
                                .description("운영 서버")));
    }
}