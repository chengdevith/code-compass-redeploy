package kh.edu.istad.codecompass.dto.creatorRequest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kh.edu.istad.codecompass.enums.Role;
import org.hibernate.validator.constraints.Length;

public record UpdateRoleRequest(
        @NotBlank(message = "Username is required")
        String username,
        String description
) { }