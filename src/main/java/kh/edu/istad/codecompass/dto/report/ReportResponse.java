package kh.edu.istad.codecompass.dto.report;

import kh.edu.istad.codecompass.domain.Comment;
import kh.edu.istad.codecompass.domain.Problem;
import kh.edu.istad.codecompass.domain.User;

import java.time.LocalDateTime;

public record ReportResponse(

        String reason,
        LocalDateTime createAt,
        String status,
        Long commentId,
        Long problemId,
        String username

) {
}
