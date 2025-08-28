package kh.edu.istad.codecompass.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BadgeRequest(
        String name,
        String description,
        String icon_url,
        LocalDateTime createdAt
) {
}
