package kh.edu.istad.codecompass.mapper;

import kh.edu.istad.codecompass.domain.Comment;
import kh.edu.istad.codecompass.dto.comment.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "problemId", source = "problem.id")
    CommentResponse toCommentResponse(Comment comment);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "problemId", source = "problem.id")
    List<CommentResponse> toCommentResponses(List<Comment> comments);

}
