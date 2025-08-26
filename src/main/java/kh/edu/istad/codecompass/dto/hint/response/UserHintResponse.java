package kh.edu.istad.codecompass.dto.hint.response;

public record UserHintResponse(
        String hint,
        Boolean isLocked
) {
}
