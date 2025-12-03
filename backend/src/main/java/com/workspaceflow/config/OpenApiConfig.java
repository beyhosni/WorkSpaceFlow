package com.workspaceflow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI workspaceFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("WorkSpaceFlow API")
                        .description("Workflow Management System with Kafka Event Bus")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("WorkSpaceFlow Team")
                                .email("contact@workspaceflow.com")));
    }
}
