package kh.edu.istad.codecompass.dto;

import kh.edu.istad.codecompass.enums.Gender;

import java.time.LocalDate;

public record UpdateUserProfileRequest(

        Gender gender,
        LocalDate dob,

        String location,
        String website,
        String github,
        String linkedin,
        String imageUrl

) {
}
