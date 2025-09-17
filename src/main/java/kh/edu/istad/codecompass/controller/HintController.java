package kh.edu.istad.codecompass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.edu.istad.codecompass.service.HintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hints")
public class HintController {

    private final HintService hintService;

    @PutMapping("/{id}/unlock")
    @Operation(summary = "Use subscriber's earned coins to unlock a hint (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public ResponseEntity<String> unlockHint(@PathVariable long id, @AuthenticationPrincipal Jwt jwt) {

        String username = jwt.getClaim("preferred_username");

        if (hintService.unlockHint(id, username))
            return ResponseEntity.ok("Hint is unlocked successfully");

        return ResponseEntity.badRequest().body("Hint is not locked, you don't have enough coins");

    }

}
