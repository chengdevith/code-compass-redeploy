package kh.edu.istad.codecompass.dto.packageDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.edu.istad.codecompass.dto.badge.response.BadgesResponse;
import kh.edu.istad.codecompass.dto.problem.response.ProblemSummaryResponse;
import kh.edu.istad.codecompass.enums.Status;
import lombok.Builder;

import java.util.List;

@Builder
public record PackageResponse(
        Long id,
        String name,
        String description,
        List<ProblemSummaryResponse> problems,
        Status status,
        String author,
        BadgesResponse badgesResponse,
        @JsonProperty("is_deleted")
        Boolean isDeleted,
        @JsonProperty("is_verified")
        Boolean isVerified

) { }
