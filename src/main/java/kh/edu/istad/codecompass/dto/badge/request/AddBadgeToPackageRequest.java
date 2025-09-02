package kh.edu.istad.codecompass.dto.badge.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record AddBadgeToPackageRequest(
        @NotEmpty(message = "Package name is required")
        @JsonProperty("package_name")
        String packageName,
        @NotBlank(message = "Badge name is required")
        @JsonProperty("badge_name")
        String badgeName
) {
}
