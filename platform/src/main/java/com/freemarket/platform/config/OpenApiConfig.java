package com.freemarket.platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * JSON - <a href="http://localhost:8080/v3/api-docs">...</a>
     * UI - <a href="http://localhost:8080/swagger-ui.html">...</a>
     */

    @Bean
    public OpenAPI freemarketApi() {
        return new OpenAPI().info(new Info()
        .title("Freemarket API").version("v1").description("MVP API for posts and ratings"));
    }
}
