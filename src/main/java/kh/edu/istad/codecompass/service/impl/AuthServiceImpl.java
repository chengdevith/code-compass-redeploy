package kh.edu.istad.codecompass.service.impl;

import jakarta.ws.rs.core.Response;
import kh.edu.istad.codecompass.dto.AssignRoleRequest;
import kh.edu.istad.codecompass.dto.RegisterRequest;
import kh.edu.istad.codecompass.dto.RegisterResponse;
import kh.edu.istad.codecompass.dto.ResetPasswordRequest;
import kh.edu.istad.codecompass.enums.Gender;
import kh.edu.istad.codecompass.enums.Role;
import kh.edu.istad.codecompass.service.AuthService;
import kh.edu.istad.codecompass.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Keycloak keycloak;
    private final RoleService roleService;


    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {

        // Validate password
        if(!registerRequest.password().equals(registerRequest.confirmedPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(registerRequest.username());
        userRepresentation.setEmail(registerRequest.email());
        userRepresentation.setFirstName(registerRequest.firstName());
        userRepresentation.setLastName(registerRequest.lastName());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(false);

        // Extra field when register
        Map<String , List<String>> attribute = new HashMap<>();
        attribute.put("gender",List.of(registerRequest.gender().name()));
        userRepresentation.setAttributes(attribute);

        // Prepare credential
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(registerRequest.confirmedPassword());
        userRepresentation.setCredentials(List.of(credential));

        try(Response response = keycloak.realm("code-compass")
                .users()
                .create(userRepresentation)){

            if(response.getStatus() == HttpStatus.CREATED.value()) {
                UserRepresentation ur = keycloak.realm("code-compass")
                        .users()
                        .search(userRepresentation.getUsername(),true)
                        .stream().findFirst().orElse(null);
                assert ur != null;

                roleService.assignRole(AssignRoleRequest.builder()
                        .userId(ur.getId())
                        .roleName(Role.SUBSCRIBER.name())
                        .build());

                this.verifyEmail(ur.getId());

                return RegisterResponse.builder()
                        .username(ur.getUsername())
                        .email(ur.getEmail())
                        .firstName(ur.getFirstName())
                        .lastName(ur.getLastName())
                        .gender(ur.getAttributes().get("gender") != null ? Gender.valueOf(ur.getAttributes().get("gender").getFirst()) : null)
                        .build();
            }
        }
        return null;
    }

    @Override
    public void verifyEmail(String userId) {
        UserResource userResource = keycloak.realm("code-compass")
                .users().get(userId);
        userResource.sendVerifyEmail();
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {

        // Find the user by email
        UserRepresentation user = keycloak.realm("code-compass")
                .users()
                .searchByEmail(resetPasswordRequest.email(), true)
                .stream().findFirst()
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email")
                );

        // Get the UserResource
        UserResource userResource = keycloak.realm("code-compass")
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
        UserRepresentation user = keycloak.realm("code-compass")
                .users()
                .searchByEmail(resetPasswordRequest.email(), true)
                .stream().findFirst()
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, resetPasswordRequest.email())
                );

        // Send Link to user for reset password
        keycloak.realm("code-compass")
                .users()
                .get(user.getId())
                .executeActionsEmail(List.of("UPDATE_PASSWORD"));
    }

}
