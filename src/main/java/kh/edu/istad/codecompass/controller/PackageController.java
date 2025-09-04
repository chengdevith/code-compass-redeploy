package kh.edu.istad.codecompass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.packageDTO.request.AddProblemToPackageRequest;
import kh.edu.istad.codecompass.dto.packageDTO.request.PackageRequest;
import kh.edu.istad.codecompass.dto.packageDTO.PackageResponse;

import kh.edu.istad.codecompass.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/code-compass/packages")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @PutMapping("/add-problems")
    @Operation(summary = "Adds problems to a package", security = {@SecurityRequirement(name = "bearerAuth")})
    public PackageResponse addProblemsToPackage(@RequestBody @Valid AddProblemToPackageRequest request) {
        return  packageService.addProblemsToPackage(request);
    }

    @PutMapping("/{id}/verification")
    @Operation(summary = "Verifies package to be created", security = {@SecurityRequirement(name = "bearerAuth")})
   ResponseEntity<String>verifyPackage(@PathVariable Long id,
                                      @RequestParam(defaultValue = "true") Boolean verified){
       packageService.verifyPackage(id, verified);

       return ResponseEntity.ok("The package has bean verified successfully");

   }

    @GetMapping
    @Operation(summary = "Views all packages - both unverified and verified", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<PackageResponse>getAllPackages() {
    return packageService.getAllPackages();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Updates a specific problem", security = {@SecurityRequirement(name = "bearerAuth")})
    public PackageResponse updatePackage(@PathVariable Long id,
         @RequestBody PackageRequest packageRequest) {
        return packageService.updatePackage(id, packageRequest);

    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Creates a package", security = {@SecurityRequirement(name = "bearerAuth")})
    public PackageResponse createPackage(@RequestBody PackageRequest packageRequest) {
        return packageService.createPackage(packageRequest);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find a specific package (public)")
    PackageResponse findPackageById(@PathVariable Long id) {
        return packageService.findPackageById(id);
    }
}
