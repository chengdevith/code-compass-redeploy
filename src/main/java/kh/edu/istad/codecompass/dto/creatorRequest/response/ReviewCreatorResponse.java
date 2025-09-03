package kh.edu.istad.codecompass.dto.creatorRequest.response;

import kh.edu.istad.codecompass.enums.Level;
import kh.edu.istad.codecompass.enums.ReportStatus;
import lombok.Builder;

@Builder
public record ReviewCreatorResponse(
        String username,
        ReportStatus status,
        String description,
        Long rank,
        Integer stars,
        Level level
) { }
