package kh.edu.istad.codecompass.config;

import jakarta.transaction.Transactional;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.elasticsearch.domain.UserIndex;
import kh.edu.istad.codecompass.elasticsearch.repository.UserElasticsearchRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakEmailVerificationSyncService {

    private final WebClient keycloakWebClient;
    private final UserRepository userRepository;
    private final UserElasticsearchRepository userElasticsearchRepository;
    private final KeycloakTokenService keycloakTokenService;

    @Value("${keycloak-admin.realm}")
    private String realmName;
    private long lastProcessedTime = 0;

    @Scheduled(fixedRate = 30000) // every 30 seconds
    @Transactional
    public void syncVerifiedUsersScheduled() {
        syncVerifiedUsers();
    }

    @Transactional
    public void syncVerifiedUsers() {

        String adminToken = keycloakTokenService.getAdminToken();

        List<Map> events = keycloakWebClient.get()
                .uri("/admin/realms/{realm}/events?type=VERIFY_EMAIL", realmName)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .retrieve()
                .bodyToMono(Map[].class)
                .blockOptional()
                .map(Arrays::asList)
                .orElse(List.of());
        for (Map event : events) {
            long eventTime = ((Number) event.get("time")).longValue();
            if (eventTime > lastProcessedTime) {
                String userId = (String) event.get("userId");

                Map userMap = keycloakWebClient.get()
                        .uri("/admin/realms/{realm}/users/{id}", realmName, userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

                assert userMap != null;

                String username = (String) userMap.get("username");

                User user = userRepository.findUserByUsername(username)
                        .orElseThrow(() -> new ResponseStatusException(
                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "User not found in Postgres: " + username
                        ));
                if (user.getIsDeleted().equals(true))
                    throw new ResponseStatusException(
                            org.springframework.http.HttpStatus.NOT_FOUND,
                            "User not found in Postgres: " + username
                    );

                if (!userElasticsearchRepository.existsById(user.getId().toString())) {
                    UserIndex index = UserIndex.builder()
                            .id(user.getId().toString())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .gender(user.getGender() != null ? user.getGender().name() : null)
                            .level(user.getLevel() != null ? user.getLevel().name() : null)
                            .rank(user.getRank())
                            .totalProblemsSolved(user.getTotalProblemsSolved())
                            .location(user.getLocation())
                            .github(user.getGithub())
                            .linkedin(user.getLinkedin())
                            .imageUrl(user.getImageUrl())
                            .build();

                    userElasticsearchRepository.save(index);
                }

                lastProcessedTime = eventTime;
            }
        }
    }
}