package kh.edu.istad.codecompass.dto.comment;

import kh.edu.istad.codecompass.domain.Discussion;
import kh.edu.istad.codecompass.domain.User;

import java.time.LocalDateTime;

public record CommentResponse(

        String comment,
        LocalDateTime commentAt,
        Boolean isDeleted,
        String username,
        Long discussionId

) {
}
