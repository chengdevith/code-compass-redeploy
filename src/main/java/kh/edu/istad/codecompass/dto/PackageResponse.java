package kh.edu.istad.codecompass.dto;

import lombok.Builder;

@Builder
public record PackageResponse(
        String name,
        String description,
        Boolean isDeleted,
        Boolean isVerified
) {
}
