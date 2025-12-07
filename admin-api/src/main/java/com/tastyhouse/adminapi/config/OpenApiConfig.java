package com.tastyhouse.adminapi.config;

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
 * API 문서: http://localhost:8081/swagger-ui.html
 * OpenAPI JSON: http://localhost:8081/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI adminApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TastyHouse Admin API")
                        .description("TastyHouse 관리자용 API 문서 - 상품, 브랜드, 공지사항, FAQ 등을 관리합니다")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("TastyHouse Admin Team")
                                .email("admin@tastyhouse.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("로컬 개발 서버"),
                        new Server()
                                .url("https://admin-api.tastyhouse.com")
                                .description("운영 서버")));
    }
}