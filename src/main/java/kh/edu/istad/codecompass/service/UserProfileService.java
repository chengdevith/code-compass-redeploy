package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.user.UpdateUserProfileRequest;
import kh.edu.istad.codecompass.dto.user.UserProfileResponse;
import kh.edu.istad.codecompass.dto.user.UserResponse;

public interface UserProfileService {

    UserResponse updateUserProfile(UpdateUserProfileRequest request, Long id);

    UserProfileResponse getUserProfile(String token);

}
