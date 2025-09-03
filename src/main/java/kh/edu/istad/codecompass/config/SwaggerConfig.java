package kh.edu.istad.codecompass.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Code Compass",
                version = "V1",
                description = "CodeCompass is an innovative online platform designed for coders of all skill levels to sharpen their programming skills and tackle coding challenges similar to LeetCode. With a user-friendly interface, CodeCompass offers a diverse range of coding tasks across various programming languages, allowing users to solve problems, test their solutions, and improve their algorithmic thinking. Whether you're preparing for technical interviews or simply looking to enhance your coding expertise, CodeCompass provides a dynamic and engaging environment to practice, learn, and grow."
        )
)
public class SwaggerConfig {
}
