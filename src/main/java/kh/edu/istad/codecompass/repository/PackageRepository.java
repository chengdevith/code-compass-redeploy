package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Package;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PackageRepository extends JpaRepository<Package, Long> {

    Optional<Package> findPackageByIdAndIsVerifiedFalse(Long id);
    Optional<Package> findByIdAndIsVerifiedTrue(Long id);
}
