package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.CreatorRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreatorRequestRepository extends JpaRepository<CreatorRequest,Long> {

    Optional<CreatorRequest> findCreatorRequestByUser_Id(Long creatorId);

}
