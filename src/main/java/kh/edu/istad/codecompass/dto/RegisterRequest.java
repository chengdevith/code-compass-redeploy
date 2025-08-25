package kh.edu.istad.codecompass.dto;

import kh.edu.istad.codecompass.enums.Gender;

public record RegisterRequest(
        String username,
        String email,
        String password,
        String confirmedPassword,
        String firstName,
        String lastName,
        Gender gender
) {
}
