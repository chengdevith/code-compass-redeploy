package kh.edu.istad.codecompass.service;

import co.elastic.clients.elasticsearch.security.UserProfile;
import kh.edu.istad.codecompass.dto.UpdateUserProfileRequest;
import kh.edu.istad.codecompass.dto.UserResponse;

public interface UserProfileService {

    UserResponse updateUserProfile(UpdateUserProfileRequest request, Long id);

}
