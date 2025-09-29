package kh.edu.istad.codecompass.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.edu.istad.codecompass.enums.Gender;
import kh.edu.istad.codecompass.enums.Status;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserResponse(
        String username,
        Gender gender,
        String dob,

        String location,
        String website,
        String github,
        String linkedin,
        @JsonProperty("image_url")
        String imageUrl,
        Status status
) {
}
