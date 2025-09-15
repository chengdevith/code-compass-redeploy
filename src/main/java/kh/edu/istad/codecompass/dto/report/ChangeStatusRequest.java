package kh.edu.istad.codecompass.dto.report;

import kh.edu.istad.codecompass.enums.ReportStatus;

public record ChangeStatusRequest(
        Long id,
        ReportStatus status
) {
}
