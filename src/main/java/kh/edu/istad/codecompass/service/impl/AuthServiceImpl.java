package kh.edu.istad.codecompass.service.impl;

import jakarta.ws.rs.core.Response;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.AssignRoleRequest;
import kh.edu.istad.codecompass.dto.auth.RegisterRequest;
import kh.edu.istad.codecompass.dto.auth.RegisterResponse;
import kh.edu.istad.codecompass.dto.ResetPasswordRequest;
import kh.edu.istad.codecompass.elasticsearch.domain.UserIndex;
import kh.edu.istad.codecompass.elasticsearch.repository.UserElasticsearchRepository;
import kh.edu.istad.codecompass.enums.Gender;
import kh.edu.istad.codecompass.enums.Role;
import kh.edu.istad.codecompass.repository.UserRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final Keycloak keycloak;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final UserElasticsearchRepository userElasticsearchRepository;

    @Value("${keycloak-admin.realm}")
    private String realmName;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {

        // Validate password
        if (!registerRequest.password().equals(registerRequest.confirmPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords don't match");


        if (userRepository.existsByUsername(registerRequest.username()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");


        log.info("Register request: {}", registerRequest);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(registerRequest.username());
        userRepresentation.setEmail(registerRequest.email());
        userRepresentation.setFirstName(registerRequest.firstName());
        userRepresentation.setLastName(registerRequest.lastName());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(false);

        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("gender", List.of(registerRequest.gender().name()));

        userRepresentation.setAttributes(attributes);

        log.info("userRepresentation: {}", userRepresentation);

        // Prepare credential
        CredentialRepresentation cr = new  CredentialRepresentation();
        cr.setType(CredentialRepresentation.PASSWORD);
        cr.setValue(registerRequest.confirmPassword());
        userRepresentation.setCredentials(List.of(cr));

        log.info("credentials: {}", userRepresentation.getCredentials());

        try (Response response = keycloak.realm(realmName)
                .users()
                .create(userRepresentation)) {

            log.info("createUserResponse: {}", response.getStatus());

            if (response.getStatus() == HttpStatus.CREATED.value()) {

                UserRepresentation ur = keycloak.realm(realmName)
                        .users()
                        .search(userRepresentation.getUsername(), true)
                        .stream()
                        .findFirst()
                        .orElse(null);

                assert ur != null;

                AssignRoleRequest assignRoleRequest = AssignRoleRequest
                        .builder()
                        .userId(ur.getId())
                        .roleName(Role.SUBSCRIBER)
                        .build();

                roleService.assignRole(assignRoleRequest);

                this.verifyEmail(ur.getId());

                User user = new User();
                user.setUsername(ur.getUsername());
                user.setEmail(ur.getEmail());

                // Get gender from UserRepresentation, convert to enum and set to user
                user.setGender(Gender.valueOf(ur.getAttributes().get("gender").getFirst()));

                user.setIsDeleted(false);
                user.setCoin(20);
                user.setStar(0);
                user.setTotal_problems_solved(0);
                user.setRank(userRepository.count() + 1);
                user.updateLevel();

                userRepository.save(user);

                // Save in Elasticsearch
                UserIndex index = UserIndex.builder()
                        .id(user.getId().toString())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .gender(user.getGender().name())
                        .level(user.getLevel().name())
                        .rank(user.getRank())
                        .totalProblemsSolved(user.getTotal_problems_solved())
                        .build();

                userElasticsearchRepository.save(index);
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
        UserResource userResource = keycloak.realm(realmName)
                .users().get(userId);
        userResource.sendVerifyEmail();
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {

        // Find the user by email
        UserRepresentation user = keycloak.realm(realmName)
                .users()
                .searchByEmail(resetPasswordRequest.email(), true)
                .stream().findFirst()
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email")
                );

        // Get the UserResource
        UserResource userResource = keycloak.realm(realmName)
                .users().get(user.getId());

        // Create new credential
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(resetPasswordRequest.newPassword());
        credential.setTemporary(false); // false = permanent

        // Reset password
        userResource.resetPassword(credential);
    }

    @Override
    public void requestPasswordReset(ResetPasswordRequest resetPasswordRequest) {

        // Find the user by email
        UserRepresentation user = keycloak.realm(realmName)
                .users()
                .searchByEmail(resetPasswordRequest.email(), true)
                .stream().findFirst()
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, resetPasswordRequest.email())
                );
        // Send Link to user for reset password
        keycloak.realm(realmName)
                .users()
                .get(user.getId())
                .executeActionsEmail(List.of("UPDATE_PASSWORD"));
    }

}
