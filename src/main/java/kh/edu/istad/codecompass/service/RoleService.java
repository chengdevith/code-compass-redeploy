package kh.edu.istad.codecompass.service;

public interface RoleService {

    /**
     * Assigns a specific role to a user.
     * <p>
     * This method is used to grant a user administrative privileges or
     * other permissions by assigning them a named role.
     *
     * @param userId The unique identifier of the user to whom the role will be assigned.
     * This ID can be an email, username, or a system-generated ID.
     * @param roleName The name of the role to be assigned.
     * The name is case-sensitive and must match a predefined role.
     * @author Panharoth
     */
    void assignRole(String userId, String roleName);
}
