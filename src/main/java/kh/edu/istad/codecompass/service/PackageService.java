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

    /**
     * Verifies or un-verifies a problem package, controlling its visibility to subscribers.
     * <p>
     * This administrative method is used to approve a package for public access (verification)
     * or to remove it from the public view (un-verification) if it requires review or editing.
     *
     * @param id The unique identifier of the package to be verified or un-verified.
     * @param isVerified A {@link Boolean} flag; {@code true} to verify the package and make it public, or {@code false} to un-verify it and restrict access.
     * @return A {@link PackageResponse} object reflecting the updated status and details of the modified package.
     * @author Bunvarn
     */
    PackageResponse verifyPackage(Long id, Boolean isVerified);

    /**
     * Retrieves a list of all existing problem packages.
     * <p>
     * This method is specifically for administrators to view every package
     * in the system.
     *
     * @return A {@link List} of {@link PackageResponse} objects, detailing all packages
     * currently in the system. The list will be empty if no packages have been created.
     * @author Bunvarn
     */
    List<PackageResponse> getAllPackages();

    /**
     * Updates an existing problem package, allowing only the original creator to make modifications.
     * <p>
     * This method is used by the package author to edit details such as the package name,
     * description, or to modify the list of problems it contains.
     *
     * @param id             The unique identifier of the package that needs to be updated.
     * @param packageRequest A {@link PackageRequest} object containing the new data for the package.
     * @param username       The username of the user attempting to update the package. This is used to verify authorization (must be the package creator).
     * @return A {@link PackageResponse} object reflecting the updated state and details of the modified package.
     * @author Bunvarn
     */
    PackageResponse updatePackage(Long id, PackageRequest packageRequest, String username);


    /**
     * Creates a new problem package in the system.
     * <p>
     * This method is accessible to both problem creators and administrators.
     *
     * @param packageRequest A {@link PackageRequest} object containing the necessary details for the new package.
     * @param username       The username of the user (creator or administrator) who is creating the package. This is used for ownership tracking.
     * @return A {@link PackageResponse} object that reflects the newly created package, including its unique ID and current status.
     * @author Bunvarn
     */
    PackageResponse createPackage(PackageRequest packageRequest, String username);

    /**
     * Retrieves a specific problem package by its unique identifier.
     * <p>
     * This method is publicly accessible, allowing any user (creator, admin, or subscriber)
     * to fetch the details of an existing package using its ID.
     *
     * @param id The unique identifier of the package to be retrieved.
     * @return A {@link PackageResponse} object containing the details of the requested package.
     * @author Bunvarn
     */
    PackageResponse findPackageById(Long id);

    /**
     * Retrieves a list of all problem packages created by a specific user.
     * <p>
     * This method is used by creators and administrators to view the collection of
     * packages authored by the user identified by the provided username.
     *
     * @param username The unique username of the creator or administrator whose packages are to be retrieved.
     * @return A {@link List} of {@link PackageResponse} objects, detailing all packages
     * created by the specified user. The list will be empty if the user has not created any packages.
     * @author Panharoth
     */
    List<PackageResponse> getPackagesByCreator(String username);

    /**
     * Retrieves a list of all verified problem packages.
     * <p>
     * This method is the primary way for subscribers and the public to access
     * the official, curated collection of packages that have been approved by
     * an administrator.
     *
     * @return A {@link List} of {@link PackageResponse} objects, detailing all packages
     * that are currently marked as verified. The list will be empty if no packages have been verified yet.
     * @author Panharoth
     */
    List<PackageResponse> getAllVerifiedPackages();

    /**
     * Retrieves a list of all unverified problem packages.
     * <p>
     * This method is generally intended for administrators to easily review and manage
     * packages that are pending approval before being made public to subscribers.
     *
     * @return A {@link List} of {@link PackageResponse} objects, detailing all packages
     * that have not yet been marked as verified. The list will be empty if all existing packages are verified.
     * @author Panharoth
     */
    List<PackageResponse> getAllUnverifiedPackages();

    void deletePackageById(Long id, String username);

    void rejectPackage(Long id);
}
