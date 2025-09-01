package kh.edu.istad.codecompass.dto.packageDTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PackageRequest(
        @NotBlank(message = "Package name is required")
        String name,
        @NotBlank(message = "Package description is required")

        String description
) {
}
