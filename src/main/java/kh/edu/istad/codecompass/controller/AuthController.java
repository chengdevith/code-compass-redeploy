package kh.edu.istad.codecompass.controller;

import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.auth.RegisterRequest;
import kh.edu.istad.codecompass.dto.auth.RegisterResponse;
import kh.edu.istad.codecompass.dto.ResetPasswordRequest;
import kh.edu.istad.codecompass.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/code-compass/auth")
public class AuthController {

    private final AuthService authService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public RegisterResponse register(@RequestBody @Valid RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok("Reset Password successfully");
    }

    @PostMapping("/request-reset-password")
    public ResponseEntity<String> requestResetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        authService.requestPasswordReset(resetPasswordRequest);
        return ResponseEntity.ok("We have sent link to your email to reset password");
    }
}
