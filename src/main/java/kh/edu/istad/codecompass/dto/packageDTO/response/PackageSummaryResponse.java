package kh.edu.istad.codecompass.dto.packageDTO.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.edu.istad.codecompass.dto.badge.response.BadgesResponse;
import kh.edu.istad.codecompass.enums.Status;
import lombok.Builder;

@Builder
public record PackageSummaryResponse(
            Long id,
            String name,
            String description,
            Status status,
            String author,
            @JsonProperty("badges_response")
            BadgesResponse badgesResponse,
            @JsonProperty("is_deleted")
            Boolean isDeleted,
            @JsonProperty("is_verified")
            Boolean isVerified,
            @JsonProperty("total_problems")
            Long totalProblems

    ) { }
