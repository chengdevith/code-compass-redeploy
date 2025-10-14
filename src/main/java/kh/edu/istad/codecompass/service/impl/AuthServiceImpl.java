package kh.edu.istad.codecompass.service.impl;

import jakarta.ws.rs.core.Response;
import kh.edu.istad.codecompass.domain.LeaderBoard;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.auth.RegisterResponse;
import kh.edu.istad.codecompass.dto.auth.request.AssignRoleRequest;
import kh.edu.istad.codecompass.dto.auth.request.RegisterRequest;
import kh.edu.istad.codecompass.dto.auth.request.ResetPasswordRequest;
import kh.edu.istad.codecompass.enums.OAuthProvider;
import kh.edu.istad.codecompass.enums.Gender;
import kh.edu.istad.codecompass.enums.Role;
import kh.edu.istad.codecompass.enums.Status;
import kh.edu.istad.codecompass.repository.LeaderBoardRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.AuthService;
import kh.edu.istad.codecompass.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.FederatedIdentityRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final Keycloak keycloak;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final LeaderBoardRepository leaderBoardRepository;

    @Value("${keycloak-admin.realm}")
    private String realmName;

    private final Map<String, Object> emailLocks = new ConcurrentHashMap<>();

    // A helper method to get a user resource from Keycloak with error handling
    private UserResource getUserResource(String userId) {
        try {
            return keycloak.realm(realmName).users().get(userId);
        } catch (jakarta.ws.rs.NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in Keycloak");
        }
    }

    // A helper method to get a user representation from Keycloak with error handling
    private UserRepresentation getUserRepresentation(String userId) {
        try {
            UserRepresentation userRep = getUserResource(userId).toRepresentation();
            if (userRep == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in Keycloak");
            }
            return userRep;
        } catch (jakarta.ws.rs.NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in Keycloak");
        }
    }

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        Object lock = emailLocks.computeIfAbsent(registerRequest.email(), k -> new Object());

        synchronized (lock) {
            try {
                return createUserSynchronized(registerRequest);
            } finally {
                emailLocks.remove(registerRequest.email(), lock);
            }
        }
    }

    @Override
    public void verifyEmail(String userId) {
        log.info("Verifying email for user {} in realm {}", userId, realmName);
        getUserResource(userId).sendVerifyEmail();
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        // Find the user by email
        UserRepresentation user = keycloak.realm(realmName)
                .users()
                .searchByEmail(resetPasswordRequest.email(), true)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email"));

        // Get the UserResource and reset the password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(resetPasswordRequest.newPassword());
        credential.setTemporary(false);

        getUserResource(user.getId()).resetPassword(credential);
    }

    @Override
    public void requestPasswordReset(ResetPasswordRequest resetPasswordRequest) {
        // Find the user by email
        UserRepresentation user = keycloak.realm(realmName)
                .users()
                .searchByEmail(resetPasswordRequest.email(), true)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Send Link to user for reset password
        getUserResource(user.getId()).executeActionsEmail(List.of("UPDATE_PASSWORD"));
    }

    @Override
    public void handleOAuthUserRegistration(String keycloakUserId) {
        UserRepresentation keycloakUser = getUserRepresentation(keycloakUserId);
        OAuthProvider provider = getOAuthProvider(keycloakUserId);

        if (provider == OAuthProvider.NONE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only OAuth users can be synced");
        }

        if (!userRepository.existsByUsername(keycloakUser.getUsername())) {
            User user = new User();
            user.setUsername(keycloakUser.getUsername());
            setFromKeycloakToLocalUser(keycloakUser, user);
            user.setAuthProvider(provider);

            userRepository.save(user);
        }
    }


    // ============================ Helper methods ============================

    private RegisterResponse createUserSynchronized(RegisterRequest request) {
        // Centralized checks for existing users
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists: " + request.email());
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists: " + request.username());
        }

        // Check if user exists in Keycloak by email
        List<UserRepresentation> existingKeycloakUsersByEmail = keycloak.realm(realmName)
                .users()
                .search(null, null, null, request.email(), 0, 1);

        boolean userExistsInKeycloakByEmail = !existingKeycloakUsersByEmail.isEmpty();

        if (userExistsInKeycloakByEmail) {
            UserRepresentation existingKeycloakUser = existingKeycloakUsersByEmail.getFirst();
            OAuthProvider actualProvider = getOAuthProvider(existingKeycloakUser.getId());

            if (actualProvider != OAuthProvider.NONE) {
                log.info("Detected OAuth user: {} with provider: {}", request.email(), actualProvider);
                return handleOAuthUser(existingKeycloakUser, request, actualProvider);
            } else {
                // If user exists in Keycloak but is NOT an OAuth user
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists: " + request.email());
            }
        }

        // If user doesn't exist anywhere, create a new user (non-OAuth flow)
        return createNewNonOAuthUser(request);
    }

    private RegisterResponse createNewNonOAuthUser(RegisterRequest registerRequest) {
        // Validate password match
        if (!registerRequest.password().equals(registerRequest.confirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords don't match");
        }

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

        CredentialRepresentation cr = new CredentialRepresentation();
        cr.setType(CredentialRepresentation.PASSWORD);
        cr.setValue(registerRequest.confirmPassword());
        userRepresentation.setCredentials(List.of(cr));

        try (Response response = keycloak.realm(realmName).users().create(userRepresentation)) {
            if (response.getStatus() != HttpStatus.CREATED.value()) {
                String errorBody = response.readEntity(String.class);
                log.error("Failed to create user in Keycloak. Status: {}, Error: {}", response.getStatus(), errorBody);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user in Keycloak: " + errorBody);
            }

            // Extract the user ID directly from the Location header
            String location = response.getLocation().toString();
            String userId = location.substring(location.lastIndexOf('/') + 1);

            UserRepresentation createdKeycloakUser = getUserRepresentation(userId);

            roleService.assignRole(AssignRoleRequest.builder().userId(userId).roleName(Role.SUBSCRIBER).build());
            this.verifyEmail(userId);

            User localUser = new User();
            localUser.setUsername(createdKeycloakUser.getUsername());
            setFromKeycloakToLocalUser(createdKeycloakUser, localUser);
            localUser.setAuthProvider(OAuthProvider.NONE);

            LeaderBoard leaderBoard = leaderBoardRepository.findById(1L).orElseGet(LeaderBoard::new);
            localUser.setLeaderBoard(leaderBoard);
            leaderBoard.getUsers().add(localUser);
            leaderBoardRepository.save(leaderBoard);

            return RegisterResponse.builder()
                    .email(localUser.getEmail())
                    .firstName(registerRequest.firstName())
                    .lastName(registerRequest.lastName())
                    .username(localUser.getUsername())
                    .build();
        } catch (Exception e) {
            log.error("Exception while creating user", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user: " + e.getMessage());
        }
    }

    private OAuthProvider getOAuthProvider(String keycloakUserId) {
        try {
            List<FederatedIdentityRepresentation> federatedIdentities = getUserResource(keycloakUserId).getFederatedIdentity();
            if (!federatedIdentities.isEmpty()) {
                String providerName = federatedIdentities.getFirst().getIdentityProvider();
                if ("google".equalsIgnoreCase(providerName)) {
                    return OAuthProvider.GOOGLE;
                } else if ("github".equalsIgnoreCase(providerName)) {
                    return OAuthProvider.GITHUB;
                }
            }
            return OAuthProvider.NONE;
        } catch (Exception e) {
            log.error("Error getting OAuth provider for user {}: {}", keycloakUserId, e.getMessage());
            return OAuthProvider.NONE;
        }
    }

    private RegisterResponse handleOAuthUser(UserRepresentation keycloakUser, RegisterRequest registerRequest, OAuthProvider provider) {
        // Find existing user in local DB based on Keycloak email
        Optional<User> existingLocalUser = userRepository.findByEmail(keycloakUser.getEmail());

        if (existingLocalUser.isPresent()) {
            User user = existingLocalUser.get();
            return RegisterResponse.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(registerRequest.firstName())
                    .lastName(registerRequest.lastName())
                    .build();
        }

        User localUser = new User();
        localUser.setUsername(keycloakUser.getUsername());
        setFromKeycloakToLocalUser(keycloakUser, localUser);
        localUser.setAuthProvider(provider);

        return RegisterResponse.builder()
                .username(localUser.getUsername())
                .email(localUser.getEmail())
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .build();
    }

    private void setFromKeycloakToLocalUser(UserRepresentation keycloakUser, User user) {
        user.setEmail(keycloakUser.getEmail());

        Map<String, List<String>> attributes = keycloakUser.getAttributes();
        if (attributes != null && attributes.containsKey("gender")) {
            user.setGender(Gender.valueOf(attributes.get("gender").getFirst()));
        } else {
            // Set a default gender if not available from Keycloak
            user.setGender(Gender.OTHER);
        }

        if (user.getGender().equals(Gender.FEMALE)) user.setImageUrl("https://cdn.jsdelivr.net/gh/alohe/avatars/png/memo_8.png");
        else if (user.getGender().equals(Gender.MALE)) user.setImageUrl("https://cdn.jsdelivr.net/gh/alohe/avatars/png/memo_34.png");
        else user.setImageUrl("https://cdn.jsdelivr.net/gh/alohe/avatars/png/memo_22.png");

        user.setIsDeleted(false);
        user.setCoin(20);
        user.setStar(0);
        user.setTotalProblemsSolved(0);
        user.setRank(userRepository.count() + 1);
        user.updateLevel();
        user.setStatus(Status.ALLOWED);
        user.setRole(Role.SUBSCRIBER);

        // Find or create leaderboard
        LeaderBoard leaderBoard = leaderBoardRepository.findById(1L).orElseGet(LeaderBoard::new);

        // Associate user with leaderboard
        user.setLeaderBoard(leaderBoard);
        leaderBoard.getUsers().add(user);

        // Save both entities to ensure relationships are persisted
//        leaderBoardRepository.save(leaderBoard);


    }
}
