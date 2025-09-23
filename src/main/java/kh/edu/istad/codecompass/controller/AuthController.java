package kh.edu.istad.codecompass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.auth.RegisterResponse;
import kh.edu.istad.codecompass.dto.auth.request.LoginRequest;
import kh.edu.istad.codecompass.dto.auth.request.RefreshTokenRequest;
import kh.edu.istad.codecompass.dto.auth.request.RegisterRequest;
import kh.edu.istad.codecompass.dto.auth.request.ResetPasswordRequest;
import kh.edu.istad.codecompass.dto.auth.response.KeycloakTokenResponse;
import kh.edu.istad.codecompass.dto.auth.response.TokenResponse;
import kh.edu.istad.codecompass.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final RestTemplate restTemplate;

    @Value("${keycloak-admin.client-id}")
    private String clientId;

    @Value("${keycloak-admin.client-secret}")
    private String clientSecret;

    @Value("${keycloak-admin.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak-admin.realm}")
    private String realm;

    @PostMapping("/login")
    @Operation(summary = "Get access token from Keycloak (public)")
    public ResponseEntity<?> getToken(@RequestBody LoginRequest request) {
        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // Prepare form data
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "password");
            formData.add("client_id", clientId);
            formData.add("username", request.username());
            formData.add("password", request.password());

            // Add client secret if it's a confidential client
            if (clientSecret != null && !clientSecret.isEmpty()) {
                formData.add("client_secret", clientSecret);
            }

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

            // Call Keycloak token endpoint
            String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(
                    tokenUrl,
                    entity,
                    KeycloakTokenResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                KeycloakTokenResponse keycloakResponse = response.getBody();

                // Return custom response with essential token info
                TokenResponse tokenResponse = TokenResponse.builder()
                        .accessToken(keycloakResponse.accessToken())
                        .refreshToken(keycloakResponse.refreshToken())
                        .tokenType(keycloakResponse.tokenType())
                        .expiresIn(keycloakResponse.expiresIn())
                        .build();

                return ResponseEntity.ok(tokenResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid credentials"));
            }

        } catch (HttpClientErrorException e) {
            log.error("Keycloak authentication failed: {}", e.getResponseBodyAsString());

            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid username or password"));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Authentication failed"));

        } catch (Exception e) {
            log.error("Unexpected error during authentication: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Authentication service unavailable"));
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token (public)")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("client_id", clientId);
            formData.add("refresh_token", request.refreshToken());

            if (clientSecret != null && !clientSecret.isEmpty()) {
                formData.add("client_secret", clientSecret);
            }

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

            String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(
                    tokenUrl,
                    entity,
                    KeycloakTokenResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                KeycloakTokenResponse keycloakResponse = response.getBody();

                TokenResponse tokenResponse = TokenResponse.builder()
                        .accessToken(keycloakResponse.accessToken())
                        .refreshToken(keycloakResponse.refreshToken())
                        .tokenType(keycloakResponse.tokenType())
                        .expiresIn(keycloakResponse.expiresIn())
                        .build();

                return ResponseEntity.ok(tokenResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid refresh token"));
            }

        } catch (Exception e) {
            log.error("Token refresh failed: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Token refresh failed"));
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    @Operation(summary = "Register account (public)")
    public RegisterResponse register(@RequestBody @Valid RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok("Reset Password successfully");
    }

    @PostMapping("/request-reset-password")
    @Operation(summary = "Request reset Password (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<String> requestResetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        authService.requestPasswordReset(resetPasswordRequest);
        return ResponseEntity.ok("We have sent link to your email to reset password");
    }
}
