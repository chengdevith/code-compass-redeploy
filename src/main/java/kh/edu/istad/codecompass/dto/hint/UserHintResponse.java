package kh.edu.istad.codecompass.dto.hint;

public record UserHintResponse(
        String hint,
        Boolean isLocked
) {
}
