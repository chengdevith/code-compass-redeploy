package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.creatorRequest.CreatorRequestDto;
import kh.edu.istad.codecompass.dto.creatorRequest.CreatorResponseDTO;

public interface CreatorRequestService {

    CreatorResponseDTO requestTobeCreator(CreatorRequestDto creatorRequestDto);

}
