package kh.edu.istad.codecompass.dto.submissionHistory.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import kh.edu.istad.codecompass.enums.Star;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SubmissionHistoryResponse(
    Integer coin,
    @JsonProperty("language_id")
    String languageId,
    Star star,
    String status,
    @JsonProperty("source_code")
    String sourceCode,
    @JsonProperty("submitted_at")
    LocalDateTime submittedAt,
    String time,
    Integer memory
) { }
