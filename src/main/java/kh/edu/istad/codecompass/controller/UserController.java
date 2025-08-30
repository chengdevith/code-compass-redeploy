package kh.edu.istad.codecompass.controller;

import kh.edu.istad.codecompass.dto.UpdateUserProfileRequest;
import kh.edu.istad.codecompass.dto.UserResponse;
import kh.edu.istad.codecompass.elasticsearch.domain.UserIndex;
import kh.edu.istad.codecompass.elasticsearch.service.UserIndexService;
import kh.edu.istad.codecompass.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/code-compass/users")
@RequiredArgsConstructor
public class UserController {

    private final UserIndexService userIndexService;
    private final UserProfileService userProfileService;

    @GetMapping("/search")
    public List<UserIndex> searchUsers(@RequestParam String keyword) {
        return userIndexService.searchUsers(keyword);
    }

    @PatchMapping("update/{id}")
    public UserResponse updateUser(@RequestBody UpdateUserProfileRequest request, @PathVariable long id) {
       return userProfileService.updateUserProfile(request,id);
    }
}
