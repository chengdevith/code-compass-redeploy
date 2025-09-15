package kh.edu.istad.codecompass.dto.user;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserResponse(
        String gender,
        LocalDate dob,

        String location,
        String website,
        String github,
        String linkedin,
        String imageUrl
) {
}
