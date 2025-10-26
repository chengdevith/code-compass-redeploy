package kh.edu.istad.codecompass.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.packageDTO.request.AddProblemToPackageRequest;
import kh.edu.istad.codecompass.dto.packageDTO.request.PackageRequest;
import kh.edu.istad.codecompass.dto.packageDTO.response.PackageResponse;

import kh.edu.istad.codecompass.dto.packageDTO.response.PackageSummaryResponse;
import kh.edu.istad.codecompass.dto.problem.response.UserProblemResponse;
import kh.edu.istad.codecompass.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/packages")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @PutMapping("/add-problems")
    @Operation(summary = "Adds problems to a package | [ CREATOR, ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public PackageResponse addProblemsToPackage(@RequestBody @Valid AddProblemToPackageRequest request) {
        return  packageService.addProblemsToPackage(request);
    }

    @PutMapping("/{id}/verification")
    @Operation(summary = "Verify a package | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    PackageResponse verifyPackage(@PathVariable Long id, @RequestParam(defaultValue = "true") Boolean verified){
       return packageService.verifyPackage(id, verified);
   }

    @GetMapping
    @Operation(summary = "Get all packages - both verified and unverified | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public List<PackageResponse> getAllProblems(){
        return packageService.getAllPackages();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a problem | [ CREATOR ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public PackageResponse updatePackage(@PathVariable Long id,
         @RequestBody PackageRequest packageRequest, @AuthenticationPrincipal Jwt jwt) {

        String username = jwt.getClaim("preferred_username");
        return packageService.updatePackage(id, packageRequest, username);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Creates a package | [ CREATOR, ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    public PackageResponse createPackage(@RequestBody PackageRequest packageRequest, @AuthenticationPrincipal Jwt jwt) {

        String username = jwt.getClaim("preferred_username");
        return packageService.createPackage(packageRequest, username);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Find a specific package (public)")
    PackageResponse findPackageById(@PathVariable Long id) {
        return packageService.findPackageById(id);
    }

    @GetMapping("/me")
    @Operation(summary = "Get all packages by author | [ CREATOR | ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    List<PackageResponse> getPackagesByCreator(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        return packageService.getPackagesByCreator(username);
    }

    @GetMapping("/verified")
    @Operation(summary = "Getting all verified packages (public)")
    List<PackageSummaryResponse> getAllVerifiedPackages() {
        return packageService.getAllVerifiedPackages();
    }

    @GetMapping("/unverified")
    @Operation(summary = "Get all verified packages | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    List<PackageResponse> getAllUnVerifiedPackages() {
        return packageService.getAllUnverifiedPackages();
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('CREATOR')")
    @Operation(summary = "Delete a package | [ CREATOR ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePackageById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        packageService.deletePackageById(id, username);
    }


    @PutMapping("/{id}/rejection")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Reject a package | [ ADMIN ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectPackageById(@PathVariable Long id) {
        packageService.rejectPackage(id);
    }

    @GetMapping("/{packageId}/user-progress")
    @PreAuthorize("hasAnyRole('SUBSCRIBER', 'CREATOR')")
    @Operation(summary = "Get a user's progress in a package | [ SUBSCRIBER, CREATOR ] (secured)", security = {@SecurityRequirement(name = "bearerAuth")})
    UserProblemResponse userProblemResponse(@PathVariable long packageId, @AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaim("preferred_username");
        return packageService.userProblems(packageId, username);

    }


}
