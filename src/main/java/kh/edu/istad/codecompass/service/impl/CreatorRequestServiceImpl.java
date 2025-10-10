package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.CreatorRequest;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.creatorRequest.request.CreatorRequestDto;
import kh.edu.istad.codecompass.dto.creatorRequest.response.CreatorResponseDTO;
import kh.edu.istad.codecompass.dto.creatorRequest.response.ReviewCreatorResponse;
import kh.edu.istad.codecompass.dto.creatorRequest.request.UpdateRoleRequest;
import kh.edu.istad.codecompass.enums.ReportStatus;
import kh.edu.istad.codecompass.enums.Role;
import kh.edu.istad.codecompass.enums.Status;
import kh.edu.istad.codecompass.repository.CreatorRequestRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.CreatorRequestService;
import kh.edu.istad.codecompass.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreatorRequestServiceImpl implements CreatorRequestService {

    private final CreatorRequestRepository creatorRequestRepository;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final Keycloak keycloak;

    @Value("${keycloak-admin.realm}")
    private String realmName;

    @Override
    public CreatorResponseDTO requestTobeCreator(CreatorRequestDto creatorRequestDto, String username) {

        User user = userRepository.findUserByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found")
        );
        if (user.getIsDeleted().equals(true))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");

        CreatorRequest creatorRequest = new CreatorRequest();
        creatorRequest.setDescription(creatorRequest.getDescription());
        creatorRequest.setStatus(Status.PENDING);
        creatorRequest.setUser(user);

        creatorRequest = creatorRequestRepository.save(creatorRequest);

        return CreatorResponseDTO
                .builder()
                .status(creatorRequest.getStatus())
                .build();
    }

    @Override
    public List<ReviewCreatorResponse> getAllCreatorsRequest() {

        List<CreatorRequest> creatorRequests = creatorRequestRepository.findAll();
        List<ReviewCreatorResponse> reviewCreatorResponses = new ArrayList<>();

        creatorRequests.forEach(creatorRequest -> {
            User user = new User();
            user = userRepository.findUserByUsername(creatorRequest.getUser().getUsername()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found")
            );
            if (user.getIsDeleted().equals(true))
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");

            ReviewCreatorResponse response = ReviewCreatorResponse
                    .builder()
                    .username(user.getUsername())
                    .status(user.getStatus())
                    .description(creatorRequest.getDescription())
                    .level(user.getLevel())
                    .rank(user.getRank())
                    .stars(user.getStar())
                    .build();
            reviewCreatorResponses.add(response);
        });

        return reviewCreatorResponses;
    }

    @Override
    public ReviewCreatorResponse assignRoleToCreator(UpdateRoleRequest updateRoleRequest) {

        User user = userRepository.findUserByUsername(updateRoleRequest.username()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found")
        );
        if (user.getIsDeleted().equals(true))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");

        List<UserRepresentation> users = keycloak.realm(realmName).users().search(updateRoleRequest.username(), true);
        if (!users.isEmpty()) {
            String userId = users.getFirst().getId();
            UserResource userResource = keycloak.realm(realmName).users().get(userId);

            RoleRepresentation role = keycloak.realm(realmName)
                    .roles()
                    .get("CREATOR")
                    .toRepresentation();

            userResource.roles().realmLevel().add(List.of(role));

            CreatorRequest creatorRequest = creatorRequestRepository.findCreatorRequestByUser_Id(user.getId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
            );
            creatorRequest.setStatus(Status.APPROVED);
            creatorRequest = creatorRequestRepository.save(creatorRequest);

            user.setCreatorRequest(creatorRequest);
            user.setRole(Role.CREATOR);
            user = userRepository.save(user);

        }
        return ReviewCreatorResponse
                .builder()
                .username(user.getUsername())
                .status(user.getCreatorRequest().getStatus())
                .description(user.getCreatorRequest().getDescription())
                .stars(user.getStar())
                .rank(user.getRank())
                .level(user.getLevel())
                .build();
    }
}

