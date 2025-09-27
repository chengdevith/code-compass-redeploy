package kh.edu.istad.codecompass.repository;

import kh.edu.istad.codecompass.domain.Package;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Lister;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface PackageRepository extends JpaRepository<Package, Long> {

    Optional<Package> findPackageByIdAndIsVerifiedFalse(Long id);
    Optional<Package> findByIdAndIsVerifiedTrue(Long id);
    Boolean existsByName(String name);
//    Optional<Package> findByName(String name);
    Optional<Package> findByNameAndIsVerifiedTrue(String name);
//    Optional<Package> findPackageById(Long problemId);

    List<Package> findPackagesByProblems_Id(Long problemId);

    List<Package> findPackagesByAuthor(String username);

    List<Package> findPackagesByIsVerifiedTrue();

    List<Package> findPackagesByIsVerifiedFalse();
}
