package kh.edu.istad.codecompass.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.edu.istad.codecompass.enums.Gender;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record UpdateUserProfileRequest(

        Gender gender,
        LocalDate dob,
        @Length(max = 120, message = "Biography cannot be more than 120 characters")
        String bio,

        String location,
        String website,
        String github,
        String linkedin,
        @JsonProperty("image_url")
        String imageUrl

) {
}
