package kh.edu.istad.codecompass.mapper;

import kh.edu.istad.codecompass.domain.Comment;
import kh.edu.istad.codecompass.dto.comment.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "discussionId", source = "discussion.id")
    CommentResponse toCommentResponse(Comment comment);

}
