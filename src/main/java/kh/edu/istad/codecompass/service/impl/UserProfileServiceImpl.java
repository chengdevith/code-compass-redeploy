package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.UpdateUserProfileRequest;
import kh.edu.istad.codecompass.dto.UserResponse;
import kh.edu.istad.codecompass.elasticsearch.domain.UserIndex;
import kh.edu.istad.codecompass.elasticsearch.repository.UserElasticsearchRepository;
import kh.edu.istad.codecompass.mapper.UserMapper;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserElasticsearchRepository userElasticsearchRepository;

    @Override
    public UserResponse updateUserProfile(UpdateUserProfileRequest request, Long id) {

        // Step 1: Find user in Postgres
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in Postgres")
        );

        // Step 2: Update fields using mapper
        userMapper.toUserPartially(request, user);
        userRepository.save(user);

        // Step 3: Find user in Elasticsearch
        UserIndex userIndex = userElasticsearchRepository.findById(user.getId().toString())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in Elasticsearch")
                );

        // Step 4: Verify Postgres user and ES user match (by username or email)
        if (!user.getUsername().equals(userIndex.getUsername()) ||
                !user.getEmail().equals(userIndex.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User data mismatch between Postgres and Elasticsearch"
            );
        }

        // Step 5: Update ES fields
        userIndex.setGender(user.getGender() != null ? user.getGender().name() : null);
        userIndex.setLocation(user.getLocation());
        userIndex.setGithub(user.getGithub());
        userIndex.setLinkedin(user.getLinkedin());
        userIndex.setImageUrl(user.getImageUrl());

        userElasticsearchRepository.save(userIndex);

        // Step 6: Return updated response
        return userMapper.toUserResponse(user);
    }
}
