package kh.edu.istad.codecompass.dto;

public record ResetPasswordRequest(
        String email,
        String newPassword
) {
}
