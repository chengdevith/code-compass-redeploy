package kh.edu.istad.codecompass.service.impl;

import jakarta.ws.rs.core.Response;
import kh.edu.istad.codecompass.dto.auth.RegisterRequest;
import kh.edu.istad.codecompass.dto.auth.RegisterResponse;
import kh.edu.istad.codecompass.service.AuthService;
import kh.edu.istad.codecompass.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final Keycloak keycloak;
    private final RoleService roleService;

    @Value("${keycloak-admin.realm}")
    private String realName;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {

        // Validate password
        if (!registerRequest.password().equals(registerRequest.confirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Passwords don't match");
        }

        log.info("Register request: {}", registerRequest);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(registerRequest.username());
        userRepresentation.setEmail(registerRequest.email());
        userRepresentation.setFirstName(registerRequest.firstName());
        userRepresentation.setLastName(registerRequest.lastName());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(false);

        log.info("userRepresentation: {}", userRepresentation);

        // Prepare credential
        CredentialRepresentation cr = new  CredentialRepresentation();
        cr.setType(CredentialRepresentation.PASSWORD);
        cr.setValue(registerRequest.confirmPassword());
        userRepresentation.setCredentials(List.of(cr));

        log.info("credentials: {}", userRepresentation.getCredentials());

        try (Response response = keycloak.realm(realName)
                .users()
                .create(userRepresentation)) {
            log.info("createUserResponse: {}", response.getStatus());
            if (response.getStatus() == HttpStatus.CREATED.value()) {

                UserRepresentation ur = keycloak.realm(realName)
                        .users()
                        .search(userRepresentation.getUsername(), true)
                        .stream()
                        .findFirst()
                        .orElse(null);

                assert ur != null;

                roleService.assignRole(ur.getId(), "SUBSCRIBER");

                this.verifyEmail(ur.getId());

            }
            return RegisterResponse.builder()
                    .email(userRepresentation.getEmail())
                    .firstName(userRepresentation.getFirstName())
                    .lastName(userRepresentation.getLastName())
                    .build();
        }

    }

    @Override
    public void verifyEmail(String userId) {
        UserResource userResource = keycloak.realm(realName)
                .users().get(userId);
        userResource.sendVerifyEmail();
    }

}
