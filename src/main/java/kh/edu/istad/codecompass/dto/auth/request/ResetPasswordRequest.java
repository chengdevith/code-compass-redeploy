package kh.edu.istad.codecompass.dto.auth.request;

public record ResetPasswordRequest(
        String email,
        String newPassword
) {
}
