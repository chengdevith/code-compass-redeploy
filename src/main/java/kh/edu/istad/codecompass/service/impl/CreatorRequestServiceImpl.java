package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.CreatorRequest;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.creatorRequest.CreatorRequestDto;
import kh.edu.istad.codecompass.dto.creatorRequest.CreatorResponseDTO;
import kh.edu.istad.codecompass.enums.ReportStatus;
import kh.edu.istad.codecompass.repository.CreatorRequestRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.CreatorRequestService;
import kh.edu.istad.codecompass.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreatorRequestServiceImpl implements CreatorRequestService {

    private final CreatorRequestRepository creatorRequestRepository;
    private final UserRepository userRepository;
    private final RoleService roleService;

    @Override
    public CreatorResponseDTO requestTobeCreator(CreatorRequestDto creatorRequestDto) {

        User user = userRepository.findUserByUsername(creatorRequestDto.username()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        CreatorRequest creatorRequest = new CreatorRequest();
        creatorRequest.setDescription(creatorRequest.getDescription());
        creatorRequest.setStatus(ReportStatus.PENDING);
        creatorRequest.setUser(user);

        creatorRequest = creatorRequestRepository.save(creatorRequest);

        return CreatorResponseDTO
                .builder()
                .status(creatorRequest.getStatus())
                .build();
    }
}

