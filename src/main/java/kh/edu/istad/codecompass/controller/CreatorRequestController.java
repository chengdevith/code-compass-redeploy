package kh.edu.istad.codecompass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.ws.rs.PATCH;
import kh.edu.istad.codecompass.dto.creatorRequest.request.CreatorRequestDto;
import kh.edu.istad.codecompass.dto.creatorRequest.response.CreatorResponseDTO;
import kh.edu.istad.codecompass.dto.creatorRequest.response.ReviewCreatorResponse;
import kh.edu.istad.codecompass.dto.creatorRequest.request.UpdateRoleRequest;
import kh.edu.istad.codecompass.service.CreatorRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/creator-requests")
public class CreatorRequestController {

    private final CreatorRequestService creatorRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Request to be creator | [ SUBSCRIBER ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public CreatorResponseDTO requestTobeCreator (@Valid CreatorRequestDto requestDto, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        return creatorRequestService.requestTobeCreator(requestDto, username);
    }

    @GetMapping
    @Operation(summary = "Get all creators' requests | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<ReviewCreatorResponse> getAllCreatorsRequest() {
        return creatorRequestService.getAllCreatorsRequest();
    }

    @PatchMapping
    @Operation(summary = "Assign to be creator | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public ReviewCreatorResponse updateRole(@RequestBody @Valid UpdateRoleRequest request) {
        return creatorRequestService.assignRoleToCreator(request);
    }

    @PatchMapping("/rejection")
    @Operation(summary = "Reject subscriber to be creator | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    @PreAuthorize("hasRole('ADMIN')")
    public CreatorResponseDTO rejectCreatorRequest(@RequestBody @Valid UpdateRoleRequest request) {
        return creatorRequestService.rejectCreatorRequest(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current creator request for a user | [ SUBSCRIBER, CREATOR ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public CreatorResponseDTO getCreatorRequestStatus(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        return creatorRequestService.getCreatorRequestStatus(username);
    }

}
