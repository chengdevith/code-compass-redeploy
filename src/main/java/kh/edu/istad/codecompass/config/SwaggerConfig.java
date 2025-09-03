package kh.edu.istad.codecompass.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Code Compass",
                version = "V1",
                description = "polin kdor toch"
        )
)
public class SwaggerConfig {
}
