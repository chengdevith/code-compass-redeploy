package kh.edu.istad.codecompass.dto;

import lombok.Builder;

@Builder
public record AssignRoleRequest(String userId, String roleName) {
}
