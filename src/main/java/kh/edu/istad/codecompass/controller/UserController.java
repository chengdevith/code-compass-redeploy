package kh.edu.istad.codecompass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.edu.istad.codecompass.dto.user.UpdateUserProfileRequest;
import kh.edu.istad.codecompass.dto.user.UserProfileResponse;
import kh.edu.istad.codecompass.dto.user.UserResponse;
import kh.edu.istad.codecompass.elasticsearch.domain.UserIndex;
import kh.edu.istad.codecompass.elasticsearch.service.UserIndexService;
import kh.edu.istad.codecompass.service.UserProfileService;
import kh.edu.istad.codecompass.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserIndexService userIndexService;
    private final UserProfileService userProfileService;

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<UserResponse> getAllUsers() {
        return userService.getUsers();
    }

    @GetMapping("/search")
    @Operation(summary = "For searching users (public)")
    public List<UserIndex> searchUsers(@RequestParam String keyword) {
        return userIndexService.searchUsers(keyword);
    }

    @PatchMapping("update/{username}")
    @Operation(summary = "Updates user information | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public UserResponse updateUser(@RequestBody UpdateUserProfileRequest request, @PathVariable String username) {
        return userProfileService.updateUserProfile(request, username);
    }

    @DeleteMapping("delete/elastic/{id}")
    @Operation(summary = "Deletes user | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userIndexService.deleteUserIndex(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get userprofile | [ SUBSCRIBER, CREATOR ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public UserProfileResponse getUserProfile(@AuthenticationPrincipal Jwt jwt) {
        return userProfileService.getUserProfile(jwt.getClaimAsString("preferred_username"));
    }

    @GetMapping("/by-email/{email}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Check if user exists by email (public)")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

}
