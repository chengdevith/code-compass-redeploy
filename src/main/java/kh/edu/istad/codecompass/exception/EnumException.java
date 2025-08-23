package kh.edu.istad.codecompass.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class EnumException {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleServiceException (HttpMessageNotReadableException e) {

        ErrorResponse <?> errorResponse = ErrorResponse
                .builder()
                .message("Service Exception")
                .code(HttpStatus.BAD_REQUEST.value())
                .timeStamp(LocalDateTime.now())
                .details(e.getMessage())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);

    }

}
