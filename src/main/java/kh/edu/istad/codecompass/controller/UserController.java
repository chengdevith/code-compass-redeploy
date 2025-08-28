package kh.edu.istad.codecompass.controller;

import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.elasticsearch.domain.UserIndex;
import kh.edu.istad.codecompass.elasticsearch.service.UserIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/code-compass/users")
@RequiredArgsConstructor
public class UserController {

    private final UserIndexService userIndexService;

    @GetMapping("/search")
    public List<UserIndex> searchUsers(@RequestParam String keyword) {
        return userIndexService.searchUsers(keyword);
    }
}
