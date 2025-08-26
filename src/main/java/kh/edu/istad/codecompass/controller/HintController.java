package kh.edu.istad.codecompass.controller;

import kh.edu.istad.codecompass.service.HintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/code-compass/hints")
public class HintController {

    private final HintService hintService;

    @PatchMapping("/{id}")
    public ResponseEntity<String> unlockHint(@PathVariable long id, @AuthenticationPrincipal Jwt jwt) {

        String username = jwt.getClaim("preferred_username");

        if (hintService.unlockHint(id, username))
            return ResponseEntity.ok("Hint is unlocked successfully");

        return ResponseEntity.badRequest().body("Hint is not locked, you don't have enough coins");

    }

}
