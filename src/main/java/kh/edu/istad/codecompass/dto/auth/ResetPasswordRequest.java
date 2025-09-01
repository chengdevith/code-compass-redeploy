package kh.edu.istad.codecompass.dto.auth;

public record ResetPasswordRequest(
        String email,
        String newPassword
) {
}
