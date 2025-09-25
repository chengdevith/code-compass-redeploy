package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.auth.request.RegisterRequest;
import kh.edu.istad.codecompass.dto.auth.RegisterResponse;
import kh.edu.istad.codecompass.dto.auth.request.ResetPasswordRequest;

public interface AuthService {

    /**
     * Processes a user registration request. This method validates the provided user
     * data and creates a new user account if the data is valid.
     *
     * @param registerRequest A {@link RegisterRequest} object containing the user's registration details, such as username, password, and email.
     * @return A {@link RegisterResponse} indicating the outcome of the registration, which may include a success message or error codes.
     * @author Panharoth
     */
    RegisterResponse register(RegisterRequest registerRequest);

    /**
     * Verifies the email address associated with a specific user. This method is typically
     * called after a user clicks on a unique verification link sent to their email.
     *
     * @param userId The unique identifier of the user whose email is to be verified.
     * @author Panharoth
     */
    void verifyEmail(String userId);

    void handleOAuthUserRegistration(String keycloakUserId);

    void resetPassword(ResetPasswordRequest resetPasswordRequest);

    void requestPasswordReset(ResetPasswordRequest resetPasswordRequest);
}
