package kh.edu.istad.codecompass.dto.creatorRequest.request;

import org.hibernate.validator.constraints.Length;

public record CreatorRequestDto(
        @Length(min = 1, max = 400, message = "This should not be more than 400 letters")
        String description
) { }
