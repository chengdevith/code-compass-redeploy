package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.Comment;
import kh.edu.istad.codecompass.domain.Problem;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.comment.CommentResponse;
import kh.edu.istad.codecompass.dto.comment.CreateCommentRequest;
import kh.edu.istad.codecompass.mapper.CommentMapper;
import kh.edu.istad.codecompass.repository.CommentRepository;
import kh.edu.istad.codecompass.repository.ProblemRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final ProblemRepository  problemRepository;

    @Override
    public CommentResponse createComment(CreateCommentRequest createCommentRequest) {

        Comment comment = new Comment();
        comment.setComment(createCommentRequest.comment());
        comment.setCommentAt(LocalDateTime.now());
        comment.setIsDeleted(false);

        User user = userRepository.findUserByUsername(createCommentRequest.username()).orElseThrow(
               () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        comment.setUser(user);

        Problem problem = problemRepository.findById(createCommentRequest.problemId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found")
        );

        comment.setProblem(problem);

        commentRepository.save(comment);

        return commentMapper.toCommentResponse(comment);
    }

    @Override
    public List<CommentResponse> getCommentsByProblemId(Long problemId) {

        return commentMapper.toCommentResponses(commentRepository.getCommentsByProblemId(problemId));
    }
}
