package kh.edu.istad.codecompass.security;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl("http://localhost:8080")
                .realm("code-compass-api")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId("admin-cli")
                .clientSecret("Fw12zDEkORHYLrKgUpufo7cgrjepWLQa")
                .build();
    }

}
