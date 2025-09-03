package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.badge.request.AddBadgeToPackageRequest;
import kh.edu.istad.codecompass.dto.badge.request.BadgeRequest;
import kh.edu.istad.codecompass.dto.badge.BadgesResponse;

import java.util.List;

public interface BadgesService {
    void addBadgeToPackage(AddBadgeToPackageRequest request);

    void updateBadge(Long id, BadgeRequest badgeRequest);

    void verifyBadges(Long id, Boolean isVerified);

    List<BadgesResponse>unverifiedBadges();

    List<BadgesResponse>verifiedBadges();

    List<BadgesResponse> getAllBadges();

    BadgesResponse createBadge(BadgeRequest badgeRequest, String author);

    BadgesResponse getBadgeById(Long id);
}
