package kh.edu.istad.codecompass.controller;


import kh.edu.istad.codecompass.dto.RegisterRequest;
import kh.edu.istad.codecompass.dto.RegisterResponse;
import kh.edu.istad.codecompass.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/code-compass/auth")
public class AuthController {

    private final AuthService authService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

}
