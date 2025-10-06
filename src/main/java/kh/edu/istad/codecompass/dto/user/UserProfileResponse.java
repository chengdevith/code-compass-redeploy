package kh.edu.istad.codecompass.dto.user;

import kh.edu.istad.codecompass.enums.Role;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserProfileResponse(

        String username,
        String email,
        String gender,
        String dob,
        String location,
        String website,
        String github,
        String linkedin,
        String imageUrl,
        String level,
        Integer coin,
        Integer star,
        Long rank,
        Integer totalProblemsSolved,
        Boolean isDeleted,
        Integer badge,
        Integer submissionHistories,
        Integer solution,
        Integer view,
        Integer comment,
        Role role
) {
}
