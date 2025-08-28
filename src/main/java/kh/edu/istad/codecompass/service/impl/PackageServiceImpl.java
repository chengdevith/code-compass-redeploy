package kh.edu.istad.codecompass.service.impl;

import jakarta.transaction.Transactional;
import kh.edu.istad.codecompass.domain.Package;
import kh.edu.istad.codecompass.dto.PackageRequest;
import kh.edu.istad.codecompass.dto.PackageResponse;
import kh.edu.istad.codecompass.mapper.PackageMapper;
import kh.edu.istad.codecompass.repository.PackageRepository;
import kh.edu.istad.codecompass.service.PackageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;



@Service
@RequiredArgsConstructor
@Slf4j
public class PackageServiceImpl implements PackageService {
    private final PackageRepository packageRepository;
    private final PackageMapper packageMapper;

    @Transactional
    @Override
    public void verifyPackage(Long id, Boolean isVerified) {
        Package pack = packageRepository.findPackageByIdAndIsVerifiedFalse(id).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found")
        );
        pack.setIsVerified(isVerified);
        packageRepository.save(pack);
    }

    @Transactional
    @Override
    public List<PackageResponse> getAllPackages() {

        return packageRepository
                .findAll()
                .stream()
                .map(packageMapper::mappackageToPackageResponse)
                .toList();
    }

    @Transactional
    @Override
    public PackageResponse getPackage(Long id) {
        Package pack = packageRepository.findByIdAndIsVerifiedTrue(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found"));
        return packageMapper.mappackageToPackageResponse(pack);
    }

    @Override
    public PackageResponse updatePackage(Long id, PackageRequest packageRequest) {
        Package pack = packageRepository.findByIdAndIsVerifiedTrue(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND  ,"Package not found."));
        packageMapper.updatePackagePartially(packageRequest, pack);
        pack = packageRepository.save(pack);
        return packageMapper.mappackageToPackageResponse(pack);
    }

    @Override
    public PackageResponse createPackage(PackageRequest packageRequest) {
        Package pack = new Package();
        pack.setName(packageRequest.name());
        pack.setDescription(packageRequest.description());
        pack.setIsDeleted(false);
        pack.setIsVerified(false);

        pack = packageRepository.save(pack);
        return packageMapper.mappackageToPackageResponse(pack) ;
    }

    @Override
    public PackageResponse findPackageById(Long id) {

        Package pack = packageRepository.findByIdAndIsVerifiedTrue(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found")
        );

        return packageMapper.mappackageToPackageResponse(pack);
    }
}
