package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.RegisterRequest;
import kh.edu.istad.codecompass.dto.RegisterResponse;
import kh.edu.istad.codecompass.dto.ResetPasswordRequest;

public interface AuthService {

    RegisterResponse register(RegisterRequest registerRequest);

    void verifyEmail(String userId);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);

    void requestPasswordReset(ResetPasswordRequest resetPasswordRequest);
}
