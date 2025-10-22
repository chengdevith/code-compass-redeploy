package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.creatorRequest.request.CreatorRequestDto;
import kh.edu.istad.codecompass.dto.creatorRequest.response.CreatorResponseDTO;
import kh.edu.istad.codecompass.dto.creatorRequest.response.ReviewCreatorResponse;
import kh.edu.istad.codecompass.dto.creatorRequest.request.UpdateRoleRequest;

import java.util.List;

public interface CreatorRequestService {

    CreatorResponseDTO requestTobeCreator(CreatorRequestDto creatorRequestDto, String username);

    List<ReviewCreatorResponse> getAllCreatorsRequest();

    ReviewCreatorResponse assignRoleToCreator(UpdateRoleRequest updateRoleRequest);

    CreatorResponseDTO getCreatorRequestStatus(String username);

    CreatorResponseDTO rejectCreatorRequest(UpdateRoleRequest updateRoleRequest);
}
