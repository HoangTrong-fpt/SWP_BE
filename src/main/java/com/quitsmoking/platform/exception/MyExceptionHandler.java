package com.quitsmoking.platform.exception;

import com.quitsmoking.platform.dto.ErrorResponse;
import com.quitsmoking.platform.exception.exceptions.AuthenticationException;
import com.quitsmoking.platform.exception.exceptions.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class MyExceptionHandler {

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, String details) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMessage(message);
        error.setDetails(details);
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(MethodArgumentNotValidException exception){
        System.out.println("Người dùng nhập chưa đúng thông tin");
        String responseMessage = "";

        for(FieldError fieldError: exception.getFieldErrors()){
            responseMessage += fieldError.getDefaultMessage() + "\n";
        }

        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                responseMessage.trim(),
                exception.getMessage()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException exception){
        ErrorResponse error = buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                exception.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException exception) {
        ErrorResponse error = buildErrorResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                "You do not have permission to perform this action."
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
}