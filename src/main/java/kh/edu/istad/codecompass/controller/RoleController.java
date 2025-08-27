package kh.edu.istad.codecompass.controller;

import kh.edu.istad.codecompass.dto.AssignRoleRequest;
import kh.edu.istad.codecompass.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/code-compass/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("assign-role")
    public void assignRole(@RequestBody AssignRoleRequest assignRoleRequest) {
        this.roleService.assignRole(assignRoleRequest);
    }
}
