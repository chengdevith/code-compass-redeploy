package kh.edu.istad.codecompass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.edu.istad.codecompass.dto.UpdateUserProfileRequest;
import kh.edu.istad.codecompass.dto.UserResponse;
import kh.edu.istad.codecompass.elasticsearch.domain.UserIndex;
import kh.edu.istad.codecompass.elasticsearch.service.UserIndexService;
import kh.edu.istad.codecompass.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/code-compass/users")
@RequiredArgsConstructor
public class UserController {

    private final UserIndexService userIndexService;
    private final UserProfileService userProfileService;

    @GetMapping("/search")
    @Operation(summary = "For searching users", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<UserIndex> searchUsers(@RequestParam String keyword) {
        return userIndexService.searchUsers(keyword);
    }

    @PatchMapping("update/{id}")
    @Operation(summary = "Updates user information", security = {@SecurityRequirement(name = "bearerAuth")})
    public UserResponse updateUser(@RequestBody UpdateUserProfileRequest request, @PathVariable long id) {
       return userProfileService.updateUserProfile(request,id);
    }

    @DeleteMapping("delete/elastic/{id}")
    @Operation(summary = "Deletes user", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userIndexService.deleteUserIndex(id);
        return ResponseEntity.noContent().build();
    }

}
