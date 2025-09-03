package kh.edu.istad.codecompass.dto.creatorRequest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kh.edu.istad.codecompass.enums.Role;

public record UpdateRoleRequest(
        @NotNull(message = "Role is required")
        Role role,
        @NotBlank(message = "Username is required")
        String username
) { }