package kh.edu.istad.codecompass.dto.comment;

public record CreateCommentRequest(

        String comment,
        Long userId,
        Long discussionId

) {
}
