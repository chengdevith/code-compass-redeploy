package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.creatorRequest.CreatorRequestDto;
import kh.edu.istad.codecompass.dto.creatorRequest.CreatorResponseDTO;
import kh.edu.istad.codecompass.dto.creatorRequest.ReviewCreatorResponse;
import kh.edu.istad.codecompass.dto.creatorRequest.UpdateRoleRequest;

import java.util.List;

public interface CreatorRequestService {

    CreatorResponseDTO requestTobeCreator(CreatorRequestDto creatorRequestDto, String username);

    List<ReviewCreatorResponse> getAllCreatorsRequest();

    ReviewCreatorResponse assignRoleToCreator(UpdateRoleRequest updateRoleRequest);
}
