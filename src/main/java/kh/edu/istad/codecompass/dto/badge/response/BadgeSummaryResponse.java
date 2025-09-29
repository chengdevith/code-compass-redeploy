package kh.edu.istad.codecompass.dto.badge.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BadgeSummaryResponse(
        String name,
        @JsonProperty("icon_url")
        String iconUrl,
        String description
) {
}
