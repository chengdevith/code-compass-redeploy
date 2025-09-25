package kh.edu.istad.codecompass.service;

import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<?> getUserByEmail(String email);

}
