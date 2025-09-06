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
@RequestMapping("/api/v1/code-compass/badges")
@RequiredArgsConstructor
public class BadgeController {

 private final BadgesService badgesService;

    @PatchMapping("/add-to-package")
    @Operation(summary = "For adding a badge to a package (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<String> addBadgeToPackage(@RequestBody @Valid AddBadgeToPackageRequest request) {
        badgesService.addBadgeToPackage(request);
        return ResponseEntity.ok("The badge has successfully been added to package");
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Input a badge ID to update (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity<String> updateBadge (@PathVariable Long id, @RequestBody @Valid BadgeRequest badgeRequest) {
        badgesService.updateBadge(id,badgeRequest);
        return ResponseEntity.ok("The badge been updated successfully");

    }

    @GetMapping("/unverified")
    @Operation(summary = "This endpoint acts like a display filtering for admin to unverified badges (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
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
    @Operation(summary = "For verifying badges (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    ResponseEntity <String>VerifiedBadges(@PathVariable Long id, @RequestParam(defaultValue = "true")
                                          boolean verified) {
        badgesService.verifyBadges(id, verified);
        return ResponseEntity.ok("The badge has been verified successfully");

    }

    @GetMapping
    @Operation(summary = "For displaying all badges - both verified and unverified (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<BadgesResponse> getAllBadges(){
        return badgesService.getAllBadges();
    }

    @PostMapping
    @Operation(summary = "For creating a new badge (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.CREATED)
    public BadgesResponse createBadge(
            @RequestBody @Valid BadgeRequest badgeRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String author = jwt.getClaim("preferred_username");
        return badgesService.createBadge(badgeRequest, author);
    }

}
