package com.minimarket.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Minimarket API", version = "1.0", description = "API REST del sistema Minimarket"))
public class OpenApiConfig {
}
