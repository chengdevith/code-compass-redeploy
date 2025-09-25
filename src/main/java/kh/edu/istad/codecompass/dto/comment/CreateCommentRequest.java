package kh.edu.istad.codecompass.dto.comment;

public record CreateCommentRequest(

        String comment,
        String username,
        Long problemId

) {
}
