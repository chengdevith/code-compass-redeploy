package kh.edu.istad.codecompass.dto.badge;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.edu.istad.codecompass.enums.Status;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BadgesResponse(
        Long id,
        String name,
        String description,
        @JsonProperty("icon_url")
        String iconUrl,
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        @JsonProperty("is_deleted")
        Boolean isDeleted,
        @JsonProperty("is_verified")
        Boolean isVerified,
        String author,
        Status status
) {
}
