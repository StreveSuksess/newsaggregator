package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("News Analysis API")
                        .description("API for news parsing, analysis and management")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Your Name")
                                .email("your.email@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local development server")
                ))
                .components(new Components()
                        .addSchemas("ErrorResponse", new ObjectSchema()
                                .addProperty("status", new StringSchema().example("error"))
                                .addProperty("message", new StringSchema().example("Error message"))
                                .additionalProperties(false))
                        .addSchemas("SuccessResponse", new ObjectSchema()
                                .addProperty("status", new StringSchema().example("success"))
                                .addProperty("message", new StringSchema().example("Success message"))
                                .additionalProperties(false)));
    }
} 