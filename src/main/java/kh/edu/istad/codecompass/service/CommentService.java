package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.comment.CommentResponse;
import kh.edu.istad.codecompass.dto.comment.CreateCommentRequest;

import java.util.List;

public interface CommentService {

    CommentResponse createComment(CreateCommentRequest createCommentRequest);

    List<CommentResponse> getCommentsByProblemId(Long problemId);
}
