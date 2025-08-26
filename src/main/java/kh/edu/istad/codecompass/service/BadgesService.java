package kh.edu.istad.codecompass.service;

import kh.edu.istad.codecompass.dto.BadgeRequest;
import kh.edu.istad.codecompass.dto.BadgesResponse;

import java.util.List;

public interface BadgesService {

    void updateBadge(Long id, BadgeRequest badgeRequest);

    void verifyBadges(Long id, Boolean isVerified);

    List<BadgesResponse>UnverifiedBadges();

    List<BadgesResponse>VerifiedBadges();

    List<BadgesResponse> getAllBadges(BadgeRequest badgeRequest);

    BadgesResponse createBadge(BadgeRequest badgeRequest);

    BadgesResponse getBadges(Long id);
}
