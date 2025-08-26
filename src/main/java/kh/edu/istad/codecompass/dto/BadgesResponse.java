package kh.edu.istad.codecompass.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BadgesResponse(
        String name,
        String description,
        String icon_url,
        LocalDateTime createdAt,
        Boolean isDeleted,
        Boolean isVerified
) {
}
