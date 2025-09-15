package kh.edu.istad.codecompass.dto.oauth;

import lombok.Builder;

@Builder
public record SignUpResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        String username,
        boolean isVerified,
        String createdAt
) {}

