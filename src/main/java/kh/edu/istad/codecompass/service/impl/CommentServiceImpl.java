package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.Comment;
import kh.edu.istad.codecompass.domain.Discussion;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.comment.CommentResponse;
import kh.edu.istad.codecompass.dto.comment.CreateCommentRequest;
import kh.edu.istad.codecompass.mapper.CommentMapper;
import kh.edu.istad.codecompass.repository.CommentRepository;
import kh.edu.istad.codecompass.repository.DiscussionRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final DiscussionRepository discussionRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponse createComment(CreateCommentRequest createCommentRequest) {

        Comment comment = new Comment();
        comment.setComment(createCommentRequest.comment());
        comment.setCommentAt(LocalDateTime.now());
        comment.setIsDeleted(false);

        User user = userRepository.findById(createCommentRequest.userId()).orElseThrow(
               () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        comment.setUser(user);

        Discussion discussion = discussionRepository.findById(createCommentRequest.discussionId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discussion not found")
        );

        comment.setDiscussion(discussion);

        commentRepository.save(comment);

        return commentMapper.toCommentResponse(comment);
    }
}
