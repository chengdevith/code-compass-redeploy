package kh.edu.istad.codecompass.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class DTOsException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException e) {
        ErrorResponse <?> errorResponse = ErrorResponse
                .builder()
                .message(e.getBindingResult().getAllErrors().getFirst().getDefaultMessage())
                .code(e.getStatusCode().value())
                .timeStamp(LocalDateTime.now())
                .details(e.getMessage())
                .build();

        return ResponseEntity.status(e.getStatusCode().value()).body(errorResponse);
    }

}
