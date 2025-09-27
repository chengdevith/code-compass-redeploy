package kh.edu.istad.codecompass.service.impl;

import jakarta.transaction.Transactional;
import kh.edu.istad.codecompass.domain.Package;
import kh.edu.istad.codecompass.domain.Problem;
import kh.edu.istad.codecompass.dto.packageDTO.request.AddProblemToPackageRequest;
import kh.edu.istad.codecompass.dto.packageDTO.request.PackageRequest;
import kh.edu.istad.codecompass.dto.packageDTO.PackageResponse;
import kh.edu.istad.codecompass.enums.Status;
import kh.edu.istad.codecompass.mapper.PackageMapper;
import kh.edu.istad.codecompass.mapper.ProblemMapper;
import kh.edu.istad.codecompass.repository.PackageRepository;
import kh.edu.istad.codecompass.repository.ProblemRepository;
import kh.edu.istad.codecompass.service.PackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class PackageServiceImpl implements PackageService {
    private final PackageRepository packageRepository;
    private final PackageMapper packageMapper;
    private final ProblemRepository problemRepository;
    private final ProblemMapper problemMapper;

    @Override
    @Transactional
    public PackageResponse addProblemsToPackage(AddProblemToPackageRequest request) {

        Package pack = packageRepository.findByNameAndIsVerifiedTrue(request.packageName()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "package name not found")
        );

        List<Long> problemIds = request.problemIds();

        Set<Problem> problemSet = new HashSet<>();

        for (Long problemId : problemIds) {
            Problem problem = problemRepository.findProblemByIdAndIsVerifiedTrue(problemId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found")
            );
            problemSet.add(problem);
        }

        pack.setProblems(problemSet);

        pack = packageRepository.save(pack);

        return packageMapper.mapPackageToResponse(pack);
    }

    @Transactional
    @Override
    public PackageResponse verifyPackage(Long id, Boolean isVerified) {

        Package pack = packageRepository.findPackageByIdAndIsVerifiedFalse(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found")
        );
        pack.setIsVerified(isVerified);
        pack.setStatus(Status.APPROVED);
        pack = packageRepository.save(pack);
        return packageMapper.mapPackageToResponse(pack);
    }

    @Transactional
    @Override
    public List<PackageResponse> getAllPackages() {

        return packageRepository
                .findAll()
                .stream()
                .map(packageMapper::mapPackageToResponse)
                .toList();
    }

//    @Transactional
//    @Override
//    public PackageResponse getPackage(Long id) {
//
//        Package pack = packageRepository.findByIdAndIsVerifiedTrue(id).orElseThrow(
//                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found")
//        );
//
//        return packageMapper.mappackageToPackageResponse(pack);
//    }

    @Override
    public PackageResponse updatePackage(Long id, PackageRequest packageRequest) {

        Package pack = packageRepository.findByIdAndIsVerifiedTrue(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND  ,"Package not found.")
        );
        packageMapper.updatePackagePartially(packageRequest, pack);
        pack = packageRepository.save(pack);

        return packageMapper.mapPackageToResponse(pack);
    }

    @Override
    public PackageResponse createPackage(PackageRequest packageRequest, String username) {

        if (packageRepository.existsByName(packageRequest.name()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Package already exists");

        Package pack = new Package();
        pack.setName(packageRequest.name());
        pack.setDescription(packageRequest.description());
        pack.setIsDeleted(false);
        pack.setIsVerified(false);
        pack.setStatus(Status.PENDING);
        pack.setAuthor(username);

        pack = packageRepository.save(pack);

        return packageMapper.mapPackageToResponse(pack) ;
    }

    @Override
    public PackageResponse findPackageById(Long id) {

        Package pack = packageRepository.findByIdAndIsVerifiedTrue(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found")
        );

        return packageMapper.mapPackageToResponse(pack);
    }

    @Override
    public List<PackageResponse> getPackagesByCreator(String username) {
        return packageRepository.findPackagesByAuthor(username)
                .stream()
                .map(packageMapper::mapPackageToResponse)
                .toList();
    }

    @Override
    public List<PackageResponse> getAllVerifiedPackages() {
        return packageRepository.findPackagesByIsVerifiedTrue()
                .stream()
                .map(packageMapper::mapPackageToResponse)
                .toList();
    }

    @Override
    public List<PackageResponse> getAllUnverifiedPackages() {
        return packageRepository.findPackagesByIsVerifiedFalse()
                .stream()
                .map(packageMapper::mapPackageToResponse)
                .toList();
    }
}
