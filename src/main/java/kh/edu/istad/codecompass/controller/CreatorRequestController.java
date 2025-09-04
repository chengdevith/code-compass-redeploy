package kh.edu.istad.codecompass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.creatorRequest.request.CreatorRequestDto;
import kh.edu.istad.codecompass.dto.creatorRequest.response.CreatorResponseDTO;
import kh.edu.istad.codecompass.dto.creatorRequest.response.ReviewCreatorResponse;
import kh.edu.istad.codecompass.dto.creatorRequest.request.UpdateRoleRequest;
import kh.edu.istad.codecompass.service.CreatorRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/code-compass/creator-requests")
public class CreatorRequestController {

    private final CreatorRequestService creatorRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Fill form and request to be a creator in our platform", security = {@SecurityRequirement(name = "bearerAuth")})
    public CreatorResponseDTO requestTobeCreator(CreatorRequestDto requestDto, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        return creatorRequestService.requestTobeCreator(requestDto, username);
    }
    @GetMapping
    @Operation(summary = "This endpoint acts as a admin filter to view all subscribers' requests to be creators", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<ReviewCreatorResponse> getAllCreatorsRequest() {
        return creatorRequestService.getAllCreatorsRequest();
    }

    @PatchMapping
    @Operation(summary = "Grant role to subscriber to become a creator", security = {@SecurityRequirement(name = "bearerAuth")})
    public ReviewCreatorResponse updateRole(@RequestBody @Valid UpdateRoleRequest request) {
        return creatorRequestService.assignRoleToCreator(request);
    }

}
