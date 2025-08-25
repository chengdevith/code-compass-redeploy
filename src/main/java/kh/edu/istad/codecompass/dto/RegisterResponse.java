package kh.edu.istad.codecompass.dto;

import kh.edu.istad.codecompass.enums.Gender;
import lombok.Builder;

@Builder
public record RegisterResponse(
        String userId,
        String username,
        String email,
        String firstName,
        String lastName,
        Gender gender
) {
}
