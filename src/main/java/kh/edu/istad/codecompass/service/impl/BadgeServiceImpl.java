package kh.edu.istad.codecompass.service.impl;

import jakarta.transaction.Transactional;
import kh.edu.istad.codecompass.domain.Badge;
import kh.edu.istad.codecompass.dto.badge.BadgeRequest;
import kh.edu.istad.codecompass.dto.badge.BadgesResponse;
import kh.edu.istad.codecompass.mapper.BadgeMapper;
import kh.edu.istad.codecompass.repository.BadgeRepository;
import kh.edu.istad.codecompass.service.BadgesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class BadgeServiceImpl implements BadgesService {
    private final BadgeRepository badgeRepository;
    private final BadgeMapper badgeMapper;


    @Override
    public void updateBadge(Long id, BadgeRequest badgeRequest) {
    Badge badge = badgeRepository.findById(id)
            .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Badge not found"));
    badgeMapper.fromUpdateRequestToEntity(badgeRequest, badge);
    }

    @Override
    public void verifyBadges(Long id, Boolean isVerified) {
        Badge badge = badgeRepository.findBadgeByIdAndIsVerifiedFalse(id).orElseThrow(
                ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Badge not found"));
        badge.setIsVerified(isVerified);
        badgeRepository.save(badge);

    }

    @Override
    public List<BadgesResponse> UnverifiedBadges() {

        return badgeRepository
                .findBadgeByIsVerifiedFalse()
                .stream()
                .map(badgeMapper::toBadgeResponse)
                .toList();
    }


    @Transactional
    @Override
    public List<BadgesResponse> VerifiedBadges() {

        return badgeRepository.findBadgeByIsVerifiedTrue()
                .stream()
                .map(badgeMapper::toBadgeResponse)
                .toList();
    }

    @Override
    public List<BadgesResponse> getAllBadges(BadgeRequest badgeRequest) {

        return badgeRepository
                .findAll()
                .stream()
                .map(badgeMapper::toBadgeResponse).toList();
    }

    @Override
    public BadgesResponse createBadge(BadgeRequest badgeRequest) {
        if (badgeRepository.existsBadgeByName(badgeRequest.name()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Badge already exists");

        Badge badge = badgeMapper.fromRequestToEntity(badgeRequest);
        badgeRepository.save(badge);

        return badgeMapper.toBadgeResponse(badge);
    }

    @Transactional
    @Override
    public BadgesResponse getBadges(Long id) {
        Badge badge = badgeRepository.findBadgeByIdAndIsVerifiedTrue(id).orElseThrow(()
                -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Badge not found"));
        return badgeMapper.toBadgeResponse(badge);
    }
}
