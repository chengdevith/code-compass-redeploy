package kh.edu.istad.codecompass.service;


import kh.edu.istad.codecompass.dto.packageDTO.request.AddProblemToPackageRequest;
import kh.edu.istad.codecompass.dto.packageDTO.request.PackageRequest;
import kh.edu.istad.codecompass.dto.packageDTO.PackageResponse;

import java.util.List;


public interface PackageService {

    /**
     * Adds one or more problems to an existing problem package.
     * <p>
     * This method is intended for use by both problem creators and administrators
     * to organize and bundle problems into a single package.
     *
     * @param request An {@link AddProblemToPackageRequest} object containing the package ID and a list of problem IDs to be added.
     * @return A {@link PackageResponse} object that reflects the updated state of the package, including the newly added problems.
     * @author Panharoth
     */
    PackageResponse addProblemsToPackage(AddProblemToPackageRequest request);

    PackageResponse verifyPackage(Long id, Boolean isVerified);

    List<PackageResponse> getAllPackages();

//    PackageResponse getPackage(Long id);

    PackageResponse updatePackage(Long id,  PackageRequest packageRequest);

    PackageResponse createPackage(PackageRequest packageRequest, String username);

    PackageResponse findPackageById(Long id);

    List<PackageResponse> getPackagesByCreator(String username);

    List<PackageResponse> getAllVerifiedPackages();

    List<PackageResponse> getAllUnverifiedPackages();
}
