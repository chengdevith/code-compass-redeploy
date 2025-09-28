package kh.edu.istad.codecompass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.badge.request.AddBadgeToPackageRequest;
import kh.edu.istad.codecompass.dto.badge.request.BadgeRequest;
import kh.edu.istad.codecompass.dto.badge.BadgesResponse;
import kh.edu.istad.codecompass.service.BadgesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/badges")
@RequiredArgsConstructor
public class BadgeController {

 private final BadgesService badgesService;

    @PutMapping("/add-to-package")
    @Operation(summary = "Assign badge to a package | [ CREATOR, ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<String> addBadgeToPackage(@RequestBody @Valid AddBadgeToPackageRequest request) {
        badgesService.addBadgeToPackage(request);
        return ResponseEntity.ok("The badge has successfully been added to package");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a badge by ID | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<String> updateBadge (@PathVariable Long id, @RequestBody @Valid BadgeRequest badgeRequest) {
        badgesService.updateBadge(id,badgeRequest);
        return ResponseEntity.ok("The badge been updated successfully");

    }

    @GetMapping("/unverified")
    @Operation(summary = "Get unverified badges | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<BadgesResponse>getUnverifiedBadges(){
        return badgesService.unverifiedBadges();
    }

    @GetMapping("/verified")
    @Operation(summary = "View all verified badges (public)")
    public List<BadgesResponse>getVerifiedBadges(){
        return badgesService.verifiedBadges();
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Find badge a badge by its ID (public)")
    public BadgesResponse findBadgesById(@PathVariable Long id) {
        return badgesService.getBadgeById(id);
    }


    @PatchMapping("/{id}/verification")
    @Operation(summary = "Verify a badge | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    BadgesResponse VerifiedBadges(@PathVariable Long id, @RequestParam(defaultValue = "true")
                                          boolean verified) {
        return badgesService.verifyBadges(id, verified);
    }

    @GetMapping
    @Operation(summary = "Get all badges - both verified and unverified | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<BadgesResponse> getAllBadges(){
        return badgesService.getAllBadges();
    }

    @PostMapping
    @Operation(summary = "Create a new badge | [ CREATOR, ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.CREATED)
    public BadgesResponse createBadge(
            @RequestBody @Valid BadgeRequest badgeRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String author = jwt.getClaim("preferred_username");
        return badgesService.createBadge(badgeRequest, author);
    }

    @GetMapping("/me")
    @Operation(summary = "Get badges for a creator | [ CREATOR ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<BadgesResponse> getBadgesByAuthor(@AuthenticationPrincipal Jwt jwt){
        String username = jwt.getClaim("preferred_username");
        return badgesService.getBadgesByCreator(username);
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete a badge | [ CREATOR ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<?> deleteBadge(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt){
        String username = jwt.getClaim("preferred_username");
        badgesService.deleteBadgeById(id,  username);
        return ResponseEntity.noContent().build();
    }

}
