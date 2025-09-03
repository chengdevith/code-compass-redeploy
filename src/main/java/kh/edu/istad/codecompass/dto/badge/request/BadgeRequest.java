package kh.edu.istad.codecompass.dto.badge.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record BadgeRequest(
        String name,
        String description,
        @JsonProperty("icon_url")
        String iconUrl
) {
}
