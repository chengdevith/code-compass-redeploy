package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public ResponseEntity<?> getUserByEmail(String email) {
        if (userRepository.existsByEmail(email))
            return ResponseEntity.ok(userRepository.findByEmail(email));

        return ResponseEntity.notFound().build();
    }
}
