package kh.edu.istad.codecompass.dto.oauth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kh.edu.istad.codecompass.enums.OAuthProvider;
import kh.edu.istad.codecompass.enums.Gender;
import lombok.Builder;

@Builder
public record CompleteOAuthRequest(
        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        String username,

        @NotNull
        Gender gender, // "MALE" | "FEMALE" | "OTHER"

        @Email
        String email,

        @NotNull
        OAuthProvider authProvider, // GOOGLE | GITHUB

        @NotBlank
        String providerId
) {}

