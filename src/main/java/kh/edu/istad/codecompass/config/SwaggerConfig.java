package kh.edu.istad.codecompass.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        servers = {
                @Server(url = "https://code-compass.devith.it.com", description = "Production"),
                @Server(url = "http://localhost:8080", description = "Development")
        },
        info = @Info(
                title = "Code Compass",
                version = "V1",
                description = "kdmv CodeCompass is an online platform for coders to practice coding challenges. It offers diverse tasks across multiple programming languages, helping users enhance their algorithmic skills in a user-friendly environment."
        )
)
public class SwaggerConfig {

    @Value("${keycloak-admin.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak-admin.realm}")
    private String realm;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        // OAuth2 Authorization Code Flow for Keycloak
                        .addSecuritySchemes("keycloak", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl(keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/auth")
                                                .tokenUrl(keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                                                .refreshUrl(keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                                        )
                                )
                                .description("OAuth2 authentication via Keycloak")
                        )
                        // Alternative: Bearer token for direct access token usage
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT access token from Keycloak (format: Bearer <token>)")
                        )
                )
                .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement()
                        .addList("bearerAuth"));
    }
}
