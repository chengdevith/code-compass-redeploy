package kh.edu.istad.codecompass.controller;

import kh.edu.istad.codecompass.dto.comment.CommentResponse;
import kh.edu.istad.codecompass.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentWsController {

    private final CommentService commentService;
    private final SimpMessagingTemplate messagingTemplate;

    // Client sends STOMP frame to /app/init.{discussionId}
    @MessageMapping("/init.{problemId}")
    public void sendInitial(@DestinationVariable Long problemId) {

        List<CommentResponse> commentResponses = commentService.getCommentsByProblemId(problemId);

        // push the whole list back to the same topic everyone subscribes to
        messagingTemplate.convertAndSend("/topic/comments." + problemId, commentResponses);
    }

}
