package kh.edu.istad.codecompass.dto.oauth;

public record OAuthUserInfo(
        String email,
        String firstName,
        String lastName,
        String providerId,
        String username
) {}

