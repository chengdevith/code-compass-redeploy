package kh.edu.istad.codecompass.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakTokenService {

    private final WebClient keycloakWebClient;
    private final String clientId = "admin-cli";
    private final String clientSecret = "zqDaCTPP69W01T8qf3H6RWrfAyFRd0sr";
    private final String realm = "code-compass";

    public String getAdminToken() {
        Map<String, Object> response = keycloakWebClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("access_token")) {
            throw new IllegalStateException("Failed to fetch Keycloak admin token from realm " + realm);
        }

        return response.get("access_token").toString();
    }

}
