package kh.edu.istad.codecompass.dto.creatorRequest.response;

import kh.edu.istad.codecompass.enums.ReportStatus;
import lombok.Builder;

@Builder
public record CreatorResponseDTO(
        ReportStatus status
) {
}
