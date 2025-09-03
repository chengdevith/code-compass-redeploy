package kh.edu.istad.codecompass.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import kh.edu.istad.codecompass.enums.Role;
import lombok.Builder;

@Builder
public record AssignRoleRequest(
    @NotBlank(message = "User ID is required")
    String userId,

    @NotBlank(message = "User role is required")
    Role roleName
) {}
