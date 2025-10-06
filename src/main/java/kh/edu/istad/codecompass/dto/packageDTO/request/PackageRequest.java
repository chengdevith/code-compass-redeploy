package kh.edu.istad.codecompass.dto.packageDTO.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record PackageRequest(
        @NotBlank(message = "Package name is required")
        @Length(max = 255, message = "Package name cannot be more than 255 letters")
        String name,
        @NotBlank(message = "Package description is required")
        @Length(max = 255, message = "Package description cannot be more than 255 letters")
        String description
) {
}
