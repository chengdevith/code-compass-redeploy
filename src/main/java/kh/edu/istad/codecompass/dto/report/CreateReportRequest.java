package kh.edu.istad.codecompass.dto.report;

public record CreateReportRequest(

        String reason,
        Long commentId,
        Long problemId,
        Long userId

) {
}
