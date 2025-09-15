package kh.edu.istad.codecompass.dto.oauth;

import lombok.Builder;

@Builder
public record OAuthCallbackResponse(
        boolean requiresCompletion,
        UserData userData,
        ExistingUser existingUser
) {
    @Builder
    public record UserData(
            String email,
            String suggestedFirstName,
            String suggestedLastName,
            String suggestedUsername,
            String provider,
            String providerId
    ) {}

    @Builder
    public record ExistingUser(
            String id,
            String email,
            String firstName,
            String lastName
    ) {}
}
