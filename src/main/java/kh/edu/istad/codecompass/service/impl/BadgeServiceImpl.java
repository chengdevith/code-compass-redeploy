package kh.edu.istad.codecompass.service.impl;

import kh.edu.istad.codecompass.domain.Badge;
import kh.edu.istad.codecompass.domain.Package;
import kh.edu.istad.codecompass.dto.badge.response.BadgesResponse;
import kh.edu.istad.codecompass.dto.badge.request.AddBadgeToPackageRequest;
import kh.edu.istad.codecompass.dto.badge.request.BadgeRequest;
import kh.edu.istad.codecompass.enums.Status;
import kh.edu.istad.codecompass.mapper.BadgeMapper;
import kh.edu.istad.codecompass.repository.BadgeRepository;
import kh.edu.istad.codecompass.repository.PackageRepository;
import kh.edu.istad.codecompass.repository.UserRepository;
import kh.edu.istad.codecompass.service.BadgesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class BadgeServiceImpl implements BadgesService {
    private final BadgeRepository badgeRepository;
    private final BadgeMapper badgeMapper;
    private final PackageRepository packageRepository;
    private final UserRepository userRepository;

    @Override
    public void addBadgeToPackage(AddBadgeToPackageRequest request) {
        // Find package
        Package pack = packageRepository.findByNameAndIsVerifiedTrue(request.packageName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Package not found"));

        // Find badge
        Badge badge = badgeRepository.findBadgeByNameAndIsVerifiedTrue(request.badgeName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Badge not found"));

        // Check if package already has a badge
        if (pack.getBadge() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Package '" + request.packageName() + "' already has a badge assigned: " + pack.getBadge().getName());
        }

        // Check if badge is already assigned to a different package
        if (badge.getProblemPackage() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Badge '" + request.badgeName() + "' is already assigned to package: " + badge.getProblemPackage().getName());
        }

        // Assign the badge to package (both sides of the relationship)
        badge.setProblemPackage(pack);
        pack.setBadge(badge);

        // Save both entities to maintain consistency
        badgeRepository.save(badge);
        packageRepository.save(pack);
    }

    @Override
    public void updateBadge(Long id, BadgeRequest badgeRequest) {
    Badge badge = badgeRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Badge not found"));
    badgeMapper.fromUpdateRequestToEntity(badgeRequest, badge);
    badgeRepository.save(badge);
    }

    @Override
    public BadgesResponse verifyBadges(Long id, Boolean isVerified) {
        Badge badge = badgeRepository.findBadgeByIdAndIsVerifiedFalseAndIsDeletedFalse(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Badge not found"));
        badge.setIsVerified(isVerified);
        badge.setStatus(Status.APPROVED);
        badge = badgeRepository.save(badge);
        return badgeMapper.toBadgeResponse(badge);
    }

    @Override
    public List<BadgesResponse> unverifiedBadges() {

        return badgeRepository
                .findBadgeByIsVerifiedFalseAndIsDeletedFalse()
                .stream()
                .map(badgeMapper::toBadgeResponse)
                .toList();
    }


    @Override
    public List<BadgesResponse> verifiedBadges() {

        return badgeRepository.findBadgeByIsVerifiedTrue()
                .stream()
                .map(badgeMapper::toBadgeResponse)
                .toList();
    }

    @Override
    public List<BadgesResponse> getAllBadges() {

        return badgeRepository
                .findAll()
                .stream()
                .map(badgeMapper::toBadgeResponse).toList();
    }

    @Override
    public BadgesResponse createBadge(BadgeRequest badgeRequest, String author) {

        if (badgeRepository.existsBadgeByNameAndIsDeletedFalse(badgeRequest.name()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Badge name already exists");

        if (! userRepository.existsByUsername(author))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");

        Badge badge = badgeMapper.fromRequestToEntity(badgeRequest);
        badge.setAuthor(author);
        badge.setCreatedAt(LocalDateTime.now());
        badge.setIsVerified(false);
        badge.setIsDeleted(false);
        badge.setStatus(Status.PENDING);
        badgeRepository.save(badge);

        return badgeMapper.toBadgeResponse(badge);
    }

    @Override
    public BadgesResponse getBadgeById(Long id) {
        Badge badge = badgeRepository.findBadgeByIdAndIsVerifiedTrue(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Badge not found")
        );
        return badgeMapper.toBadgeResponse(badge);
    }

    @Override
    public List<BadgesResponse> getBadgesByCreator(String username) {
        return badgeRepository.findBadgesByAuthorAndIsDeletedFalse(username)
                .stream()
                .map(badgeMapper::toBadgeResponse)
                .toList();
    }

    @Override
    public void deleteBadgeById(Long id, String username) {
        Badge badge = badgeRepository.findBadgeByAuthorAndIdAndIsDeletedFalse(username, id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Badge with ID" + id + " not exists")
        );
        badge.setIsDeleted(true);
        badge.setIsVerified(false);
        badge.setName(UUID.randomUUID().toString());
        badge.setStatus(Status.REJECTED);
        badgeRepository.save(badge);
    }
}