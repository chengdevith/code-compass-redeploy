package kh.edu.istad.codecompass.dto.comment;

import java.time.LocalDateTime;

public record CommentResponse(

        String comment,
        LocalDateTime commentAt,
        Boolean isDeleted,
        String username,
        Long problemId

) {
}
