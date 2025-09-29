package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.dto.user.UserResponse;
import kh.edu.istad.codecompass.mapper.UserMapper;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public ResponseEntity<?> getUserByEmail(String email) {
        if (userRepository.existsByEmail(email))
            return ResponseEntity.ok(userRepository.findByEmail(email));

        return ResponseEntity.notFound().build();
    }

    @Override
    public List<UserResponse> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
    }
}
