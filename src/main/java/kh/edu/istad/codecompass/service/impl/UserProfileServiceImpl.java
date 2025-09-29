package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.user.UpdateUserProfileRequest;
import kh.edu.istad.codecompass.dto.user.UserProfileResponse;
import kh.edu.istad.codecompass.dto.user.UserResponse;
import kh.edu.istad.codecompass.elasticsearch.domain.UserIndex;
import kh.edu.istad.codecompass.elasticsearch.repository.UserElasticsearchRepository;
import kh.edu.istad.codecompass.mapper.UserMapper;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserElasticsearchRepository userElasticsearchRepository;
    private final JwtDecoder jwtDecoder;

    @Override
    public UserResponse updateUserProfile(UpdateUserProfileRequest request, Long id) {

        // Step 1: Find user in Postgres
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in Postgres")
        );

        // Step 2: Find user in Elasticsearch
        UserIndex userIndex = userElasticsearchRepository.findById(user.getId().toString())
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in Elasticsearch")
                );

        // Step 3: Verify Postgres user and ES user match (by username or email)
        if (!user.getUsername().equals(userIndex.getUsername()) ||
                !user.getEmail().equals(userIndex.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User data mismatch between Postgres and Elasticsearch"
            );
        }

        // Step 4: Update fields using mapper
        userMapper.toUserPartially(request, user);
        userRepository.save(user);

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

    @Override
    public UserProfileResponse getUserProfile(String username) {

            User user = userRepository.findUserByUsername(username).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found")
            );
            if (user.getIsDeleted().equals(true)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found");

            return UserProfileResponse.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .gender(String.valueOf(user.getGender()))
                    .dob(user.getDob())
                    .location(user.getLocation())
                    .website(user.getWebsite())
                    .github(user.getGithub())
                    .linkedin(user.getLinkedin())
                    .imageUrl(user.getImageUrl())
                    .level(String.valueOf(user.getLevel()))
                    .coin(user.getCoin())
                    .star(user.getStar())
                    .rank(user.getRank())
                    .totalProblemsSolved(user.getTotalProblemsSolved())
                    .isDeleted(user.getIsDeleted())
                    .badge(user.getBadges().size())
                    .submissionHistories(user.getSubmissionHistories().size())
                    .solution(user.getSolutions().size())
                    .view(0) // optional
                    .comment(user.getComments().size())
                    .build();
    }
}
