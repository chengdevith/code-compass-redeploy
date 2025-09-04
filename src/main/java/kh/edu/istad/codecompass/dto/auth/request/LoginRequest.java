package kh.edu.istad.codecompass.dto.auth.request;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
        @NotEmpty(message = "Username is required")
        String username,
        @NotEmpty(message = "Password is required")
        String password
) {
}
