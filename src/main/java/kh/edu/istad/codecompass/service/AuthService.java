package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.RegisterRequest;
import kh.edu.istad.codecompass.dto.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest registerRequest);

    void verifyEmail(String userId);

}
