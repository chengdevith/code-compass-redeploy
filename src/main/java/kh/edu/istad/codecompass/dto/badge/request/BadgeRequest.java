package kh.edu.istad.codecompass.dto.badge.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record BadgeRequest(
        @Length(min = 2, max = 50, message = "Badge name must be more than 2 letters")
        String name,
        @Length(max = 255, message = "Badge description must not be more than 255 letters")
        String description,
        @JsonProperty("icon_url")
        String iconUrl
) {
}
