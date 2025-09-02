package kh.edu.istad.codecompass.dto.auth.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kh.edu.istad.codecompass.enums.Gender;
import lombok.Builder;

@Builder
public record RegisterRequest(
        @NotBlank
        @JsonProperty("first_name")
        String firstName,
        @NotBlank
        @JsonProperty("last_name")
        String lastName,
        @NotBlank
        String username,
        @NotNull
        Gender gender,
        @NotBlank
        String password,
        @NotBlank
        @JsonProperty("confirmed_password")
        String confirmPassword,
        @Email
        String email
) {
}
