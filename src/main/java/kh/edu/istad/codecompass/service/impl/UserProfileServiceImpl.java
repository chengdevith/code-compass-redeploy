package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.User;
import kh.edu.istad.codecompass.dto.user.UpdateUserProfileRequest;
import kh.edu.istad.codecompass.dto.user.UserLanguageResponse;
import kh.edu.istad.codecompass.dto.user.UserProfileResponse;
import kh.edu.istad.codecompass.dto.user.UserResponse;
import kh.edu.istad.codecompass.elasticsearch.domain.UserIndex;
import kh.edu.istad.codecompass.elasticsearch.repository.UserElasticsearchRepository;
import kh.edu.istad.codecompass.enums.Level;
import kh.edu.istad.codecompass.mapper.UserMapper;
import kh.edu.istad.codecompass.repository.SubmissionHistoryRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserElasticsearchRepository userElasticsearchRepository;
    private final SubmissionHistoryRepository submissionHistoryRepository;

    @Override
    public UserResponse updateUserProfile(UpdateUserProfileRequest request, String username) {

        // Step 1: Find user in Postgres
        User user = userRepository.findUserByUsername(username).orElseThrow(
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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );

        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Level level = user.getLevel();

        List<UserLanguageResponse> userLanguageResponses =
                submissionHistoryRepository.countAcceptedSubmissionsByLanguage(user).stream()
                        .map(result -> new UserLanguageResponse(
                                (String) result[0],
                                (Long) result[1]
                        ))
                        .toList();

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
                .level(level.getDisplayName())
                .coin(user.getCoin())
                .star(user.getStar())
                .rank(user.getRank())
                .totalProblemsSolved(user.getTotalProblemsSolved())
                .isDeleted(user.getIsDeleted())
                .badge(user.getBadges().size())
                .submissionHistories(user.getSubmissionHistories().size())
                .solution(user.getSolutions().size())
                .role(user.getRole())
                .view(0)
                .comment(user.getComments().size())
                .userLanguageResponse(userLanguageResponses)
                .build();
    }

}
