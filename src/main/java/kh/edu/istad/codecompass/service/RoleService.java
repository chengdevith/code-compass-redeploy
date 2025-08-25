package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.AssignRoleRequest;

public interface RoleService {

    /**
     * Assigns a specific role to a user.
     * <p>
     * This method is used to grant a user administrative privileges or
     * other permissions by assigning them a named role.
     *
     * @param assignRoleRequest An {@link AssignRoleRequest} object containing the user's identifier and the name of the role to be assigned.
     * @author Panharoth
     */
    void assignRole(AssignRoleRequest assignRoleRequest);
}
