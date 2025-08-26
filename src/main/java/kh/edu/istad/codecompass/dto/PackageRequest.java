package kh.edu.istad.codecompass.dto;

import lombok.Builder;

@Builder
public record PackageRequest(
        String name,
        String description
) {
}
