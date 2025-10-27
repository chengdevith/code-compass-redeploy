package kh.edu.istad.codecompass.service.impl;

import jakarta.transaction.Transactional;
import kh.edu.istad.codecompass.domain.Package;
import kh.edu.istad.codecompass.domain.Problem;
import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.domain.UserProblem;
import kh.edu.istad.codecompass.dto.packageDTO.request.AddProblemToPackageRequest;
import kh.edu.istad.codecompass.dto.packageDTO.request.PackageRequest;
import kh.edu.istad.codecompass.dto.packageDTO.response.PackageResponse;
import kh.edu.istad.codecompass.dto.packageDTO.response.PackageSummaryResponse;
import kh.edu.istad.codecompass.dto.problem.response.ProblemAndSolvedResponse;
import kh.edu.istad.codecompass.dto.problem.response.UserProblemResponse;
import kh.edu.istad.codecompass.enums.Status;
import kh.edu.istad.codecompass.mapper.PackageMapper;
import kh.edu.istad.codecompass.repository.PackageRepository;
import kh.edu.istad.codecompass.repository.ProblemRepository;
import kh.edu.istad.codecompass.repository.UserProblemRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.PackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class PackageServiceImpl implements PackageService {
    private final PackageRepository packageRepository;
    private final PackageMapper packageMapper;
    private final ProblemRepository problemRepository;
    private final UserProblemRepository userProblemRepository;
    private final UserRepository userRepository;

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

    @Override
    public PackageResponse updatePackage(Long id, PackageRequest packageRequest, String username) {

        Package pack = packageRepository.findPackageByAuthorAndIdAndIsDeletedFalse(username, id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND  ,"Package not found.")
        );

        if (! pack.getAuthor().equals(username))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You're not the creator of this package " + username);

        packageMapper.updatePackagePartially(packageRequest, pack);
        pack = packageRepository.save(pack);

        return packageMapper.mapPackageToResponse(pack);
    }

    @Override
    public PackageResponse createPackage(PackageRequest packageRequest, String username) {

        if (packageRepository.existsByNameAndIsDeletedFalse(packageRequest.name()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Package already exists");

        Package pack = new Package();
        pack.setName(packageRequest.name());
        pack.setDescription(packageRequest.description());
        pack.setIsDeleted(false);
        pack.setIsVerified(false);
        pack.setStatus(Status.PENDING);
        pack.setAuthor(username);

        pack = packageRepository.save(pack);

        return packageMapper.mapPackageToResponse(pack);
    }

    @Override
    public PackageResponse findPackageById(Long id) {

        Package pack = packageRepository.findByIdAndIsVerifiedTrue(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found")
        );

        Set<Problem>  filteredProblem = pack.getProblems().stream().filter(p -> !p.getIsDeleted()).collect(Collectors.toSet());
        pack.setProblems(filteredProblem);
        return packageMapper.mapPackageToResponse(pack);
    }

    @Override
    public List<PackageResponse> getPackagesByCreator(String username) {
        return packageRepository.findPackagesByAuthorAndIsDeletedFalse(username)
                .stream()
                .map(pack -> {
                    // Create a copy of the package with filtered problems
                    Package filteredPackage = new Package();
                    filteredPackage.setId(pack.getId());
                    filteredPackage.setName(pack.getName());
                    filteredPackage.setAuthor(pack.getAuthor());
                    filteredPackage.setDescription(pack.getDescription());
                    filteredPackage.setIsDeleted(pack.getIsDeleted());
                    filteredPackage.setIsVerified(pack.getIsVerified());
                    filteredPackage.setStatus(pack.getStatus());
                    filteredPackage.setBadge(pack.getBadge() != null ? pack.getBadge().getIsDeleted().equals(false) ? pack.getBadge() : null : null);

                    // Filter only non-deleted problems
                    Set<Problem> nonDeletedProblems = pack.getProblems()
                            .stream()
                            .filter(problem -> !problem.getIsDeleted())
                            .collect(Collectors.toSet());

                    filteredPackage.setProblems(nonDeletedProblems);

                    return packageMapper.mapPackageToResponse(filteredPackage);
                })
                .toList();
    }

    @Override
    public List<PackageSummaryResponse> getAllVerifiedPackages() {
        return packageRepository.findPackagesByIsVerifiedTrue()
                .stream()
                .map(packageMapper::toPackageSummaryResponses)
                .toList();
    }

    @Override
    public List<PackageResponse> getAllUnverifiedPackages() {
        return packageRepository.findPackagesByIsVerifiedFalseAndIsDeletedFalse()
                .stream()
                .map(packageMapper::mapPackageToResponse)
                .toList();
    }

    @Override
    public void deletePackageById(Long id, String username) {
        Package pack = packageRepository.findPackageByAuthorAndIdAndIsDeletedFalse(username, id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found")
        );
        pack.setIsDeleted(true);
        pack.setIsVerified(false);
        pack.setName(UUID.randomUUID().toString());
        pack.setStatus(Status.REJECTED);
        packageRepository.save(pack);
    }

    @Override
    public void rejectPackage(Long id) {
        Package pack = packageRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found")
        );

        if (pack.getStatus().equals(Status.PENDING)) {
            pack.setStatus(Status.REJECTED);
            pack.setIsVerified(false);
            packageRepository.save(pack);
        }
        else if (pack.getStatus().equals(Status.REJECTED))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Package already rejected");
        else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found");

    }



    @Override
    public UserProblemResponse userProblems(Long packageId, String username) {

        // 1. Find package
        Package pack = packageRepository.findById(packageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found"));

        // 2. Find user
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 3. Get all problems in this package
        Set<Problem> packageProblems = pack.getProblems();
        long totalProblems = packageProblems.size();

        if (totalProblems == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Package has no problems or is rejected");
        }

        // 4. Get user's solved problems (only those inside this package)
        List<UserProblem> solvedInThisPackage = userProblemRepository
                .findAllByUserIdAndIsSolvedTrue(user.getId())
                .stream()
                .filter(up -> packageProblems.contains(up.getProblem()))
                .toList();

        // 5. Count solved problems
        long userProblemCount = solvedInThisPackage.size();

        // 6. Calculate percentage (rounded to 2 decimals)
        double percentage = (double) userProblemCount / totalProblems * 100.0;
        percentage = Math.round(percentage * 100.0) / 100.0;

        // 7. Build response list
        List<ProblemAndSolvedResponse> problemAndSolvedResponses = solvedInThisPackage.stream()
                .map(up -> ProblemAndSolvedResponse.builder()
                        .problemId(up.getProblem().getId())
                        .isSolved(true)
                        .build())
                .toList();

        // 8. Return final response
        return new UserProblemResponse(problemAndSolvedResponses, userProblemCount, totalProblems, percentage);
    }

}
