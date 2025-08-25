package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.dto.AssignRoleRequest;
import kh.edu.istad.codecompass.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final Keycloak keycloak;

    @Override
    public void assignRole(AssignRoleRequest assignRoleRequest) {

        UserResource userResource = keycloak.realm("code-compass")
                .users().get(assignRoleRequest.userId());

        RoleRepresentation role = keycloak.realm("code-compass")
                .roles().get(assignRoleRequest.roleName()).toRepresentation();

        userResource.roles().realmLevel().add(List.of(role));
    }
}
