package kh.edu.istad.codecompass.controller;

import kh.edu.istad.codecompass.dto.badge.BadgeRequest;
import kh.edu.istad.codecompass.dto.badge.BadgesResponse;
import kh.edu.istad.codecompass.service.BadgesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/code-compass/badges")
@RequiredArgsConstructor
public class BadgeController {

 private final BadgesService badgesService;

     @PatchMapping("/{id}")
    ResponseEntity<String>updateBadge(@PathVariable Long id,
                                      @RequestBody  BadgeRequest badgeRequest) {
        badgesService.updateBadge(id,badgeRequest);
        return ResponseEntity.ok("The badge been updated successfully");

    }

    @GetMapping("/unverified")
    public List<BadgesResponse>UnverifiedBadges(){
        return badgesService.UnverifiedBadges();
    }

    @GetMapping("/verified")
    public List<BadgesResponse>VerifiedBadges(){
        return badgesService.VerifiedBadges();
    }

    @GetMapping("/{id}")
    public BadgesResponse findBadgesById(@PathVariable Long id) {

        return badgesService.getBadges(id);
    }


    @PatchMapping("/{id}/verification")
    ResponseEntity <String>VerifiedBadges(@PathVariable Long id, @RequestParam(defaultValue = "true")
                                          boolean verified) {
        badgesService.verifyBadges(id, verified);
        return ResponseEntity.ok("The badge has been verified successfully");

    }

    @GetMapping("/all")
    public List<BadgesResponse> getAllBadges(BadgeRequest badgeRequest){
        return badgesService.getAllBadges(badgeRequest);
    }

    @GetMapping
    public BadgesResponse getAllBadges(Long id) {
        return badgesService.getBadges(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BadgesResponse createBadge(
            @RequestBody BadgeRequest badgeRequest
    ) {
        return badgesService.createBadge(badgeRequest);
    }

}
