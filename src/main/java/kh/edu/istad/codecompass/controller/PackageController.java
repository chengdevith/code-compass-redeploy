package kh.edu.istad.codecompass.controller;

import jakarta.validation.Valid;
import kh.edu.istad.codecompass.dto.packageDTO.AddProblemToPackageRequest;
import kh.edu.istad.codecompass.dto.packageDTO.PackageRequest;
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

    @PatchMapping("/add-problems")
    public PackageResponse addProblemsToPackage(@RequestBody @Valid AddProblemToPackageRequest request) {
        return  packageService.addProblemsToPackage(request);
    }

    @PatchMapping("/{id}/verification")
   ResponseEntity<String>verifyPackage(@PathVariable Long id,
                                      @RequestParam(defaultValue = "true") Boolean verified){
       packageService.verifyPackage(id, verified);

       return ResponseEntity.ok("The package has bean verified successfully");

   }

//    @GetMapping
//    public PackageResponse getPackage(Long id){
//        return packageService.getPackage(id);
//    }


    @GetMapping("/all")
    public List<PackageResponse>getAllPackages() {
    return packageService.getAllPackages();
    }

    @PatchMapping("/{id}")
    public PackageResponse updatePackage(@PathVariable Long id,
         @RequestBody   PackageRequest packageRequest) {
        return packageService.updatePackage(id, packageRequest);

    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PackageResponse createPackage(@RequestBody PackageRequest packageRequest) {
        return packageService.createPackage(packageRequest);
    }

    @GetMapping("/{id}")
    PackageResponse findPackageById(@PathVariable Long id) {
        return packageService.findPackageById(id);
    }
}
