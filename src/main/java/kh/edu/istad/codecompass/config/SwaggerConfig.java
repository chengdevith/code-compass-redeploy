package kh.edu.istad.codecompass.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Code Compass",
                version = "V1",
                description = "CodeCompass is an online platform for coders to practice coding challenges like LeetCode. It offers diverse tasks across multiple programming languages, helping users enhance their algorithmic skills and prepare for technical interviews in a user-friendly environment."
        )
)
public class SwaggerConfig {
}
