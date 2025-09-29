package kh.edu.istad.codecompass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kh.edu.istad.codecompass.dto.auth.request.AssignRoleRequest;
import kh.edu.istad.codecompass.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/assign-role")
    @Operation(summary = "Assign role to users | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public void assignRole(@RequestBody AssignRoleRequest assignRoleRequest) {
        this.roleService.assignRole(assignRoleRequest);
    }
}
