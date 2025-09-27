package kh.edu.istad.codecompass.dto.creatorRequest.response;

import kh.edu.istad.codecompass.enums.ReportStatus;
import kh.edu.istad.codecompass.enums.Status;
import lombok.Builder;

@Builder
public record CreatorResponseDTO(
        Status status
) {
}
