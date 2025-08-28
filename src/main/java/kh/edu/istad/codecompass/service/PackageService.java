package kh.edu.istad.codecompass.service;


import kh.edu.istad.codecompass.dto.PackageRequest;
import kh.edu.istad.codecompass.dto.PackageResponse;

import java.util.List;


public interface PackageService {

    void verifyPackage(Long id, Boolean isVerified);

    List<PackageResponse> getAllPackages();

    PackageResponse getPackage(Long id);

    PackageResponse updatePackage(Long id,  PackageRequest packageRequest);

    PackageResponse createPackage(PackageRequest packageRequest);

    PackageResponse findPackageById(Long id);

}
