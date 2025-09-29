package kh.edu.istad.codecompass.repository;


import kh.edu.istad.codecompass.domain.Badge;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

    Optional<Badge> findBadgeByNameAndIsVerifiedTrue(String name);

    Optional<Badge> findBadgeByIdAndIsVerifiedFalseAndIsDeletedFalse(long id);

    Optional<Badge> findBadgeByIdAndIsVerifiedTrue(long id);

    List<Badge> findBadgeByIsVerifiedFalseAndIsDeletedFalse();

    List<Badge> findBadgeByIsVerifiedTrue();

    boolean existsBadgeByNameAndIsDeletedFalse(String name);

    Optional<Badge> findBadgesByProblemPackage_Name(String name);

    List<Badge> findBadgesByAuthorAndIsDeletedFalse(String username);

    Optional<Badge> findBadgeByAuthorAndIdAndIsDeletedFalse(String author, Long id);
}
