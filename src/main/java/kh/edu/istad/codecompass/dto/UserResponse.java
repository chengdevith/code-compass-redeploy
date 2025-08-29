package kh.edu.istad.codecompass.dto;

import java.time.LocalDate;

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
