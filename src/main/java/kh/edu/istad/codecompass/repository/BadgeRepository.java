package kh.edu.istad.codecompass.repository;


import kh.edu.istad.codecompass.domain.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BadgeRepository extends JpaRepository<Badge, Long> {


    Optional<Badge> findBadgeByIdAndIsVerifiedFalse(long id);

    Optional<Badge> findBadgeByIdAndIsVerifiedTrue(long id);

    List<Badge> findBadgeByIsVerifiedFalse();

    List<Badge> findBadgeByIsVerifiedTrue();

    boolean existsBadgeByName(String name);
}
