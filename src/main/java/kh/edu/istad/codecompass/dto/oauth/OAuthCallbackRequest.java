package kh.edu.istad.codecompass.dto.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record OAuthCallbackRequest(
        String provider, // "google" | "github"
        @JsonProperty("access_token")
        String accessToken,
        String email,
        String name,
        String givenName,
        String familyName,
        String login,
        String sub,
        String id
) {}
