package kh.edu.istad.codecompass.controller;

import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.badge.request.AddBadgeToPackageRequest;
import kh.edu.istad.codecompass.dto.badge.request.BadgeRequest;
import kh.edu.istad.codecompass.dto.badge.BadgesResponse;
import kh.edu.istad.codecompass.service.BadgesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    ResponseEntity<String> addBadgeToPackage(@RequestBody @Valid AddBadgeToPackageRequest request) {
        badgesService.addBadgeToPackage(request);
        return ResponseEntity.ok("The badge has successfully been added to package");
    }

    @PatchMapping("/{id}")
    ResponseEntity<String> updateBadge (@PathVariable Long id, @RequestBody @Valid BadgeRequest badgeRequest) {
        badgesService.updateBadge(id,badgeRequest);
        return ResponseEntity.ok("The badge been updated successfully");

    }

    @GetMapping("/unverified")
    public List<BadgesResponse>getUnverifiedBadges(){
        return badgesService.unverifiedBadges();
    }

    @GetMapping("/verified")
    public List<BadgesResponse>getVerifiedBadges(){
        return badgesService.verifiedBadges();
    }

    @GetMapping("/{id}")
    public BadgesResponse findBadgesById(@PathVariable Long id) {
        return badgesService.getBadgeById(id);
    }


    @PatchMapping("/{id}/verification")
    ResponseEntity <String>VerifiedBadges(@PathVariable Long id, @RequestParam(defaultValue = "true")
                                          boolean verified) {
        badgesService.verifyBadges(id, verified);
        return ResponseEntity.ok("The badge has been verified successfully");

    }

    @GetMapping
    public List<BadgesResponse> getAllBadges(){
        return badgesService.getAllBadges();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BadgesResponse createBadge(
            @RequestBody @Valid BadgeRequest badgeRequest,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String author = jwt.getClaim("preferred_username");
        return badgesService.createBadge(badgeRequest, author);
    }

}
