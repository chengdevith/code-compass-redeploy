package kh.edu.istad.codecompass.dto.auth;

import lombok.Builder;

@Builder
public record RegisterResponse(
    String firstName,
    String lastName,
    String email
) {
}
