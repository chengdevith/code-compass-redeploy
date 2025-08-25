package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.dto.AssignRoleRequest;
import kh.edu.istad.codecompass.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final Keycloak keycloak;
    @Value("${keycloak-admin.realm}")
    private String realmName;

    @Override
    public void assignRole(AssignRoleRequest assignRoleRequest) {

        UserResource userResource = keycloak.realm(realmName)
                .users().get(assignRoleRequest.userId());

        RoleRepresentation role = keycloak.realm(realmName)
                .roles().get(assignRoleRequest.roleName().name()).toRepresentation();

        userResource.roles().realmLevel().add(List.of(role));
    }
}
