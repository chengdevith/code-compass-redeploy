package kh.edu.istad.codecompass.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ServiceException {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleServiceException (ResponseStatusException e) {

        ErrorResponse <?> errorResponse = ErrorResponse
                .builder()
                .message("Service Exception")
                .code(e.getStatusCode().value())
                .timeStamp(LocalDateTime.now())
                .details(e.getReason())
                .build();

        return ResponseEntity.status(e.getStatusCode().value()).body(errorResponse);

    }

}
