package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.user.UserResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    ResponseEntity<?> getUserByEmail(String email);

    List<UserResponse> getUsers();

}
