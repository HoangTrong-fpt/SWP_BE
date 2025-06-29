package com.quitsmoking.platform.exception;

import com.quitsmoking.platform.exception.exceptions.AuthenticationException;
import com.quitsmoking.platform.exception.exceptions.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleBadRequestException(MethodArgumentNotValidException exception){
        System.out.println("Người dùng nhập chưa đúng thông tin");
        String responseMessage = "";

        for(FieldError fieldError: exception.getFieldErrors()){
            responseMessage += fieldError.getDefaultMessage() + "\n";
        }

        return new ResponseEntity(responseMessage, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity handleAuthenticationException(AuthenticationException exception){
        return new ResponseEntity(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<com.quitsmoking.platform.dto.ErrorResponse> handleForbiddenException(ForbiddenException exception) {
        com.quitsmoking.platform.dto.ErrorResponse error = new com.quitsmoking.platform.dto.ErrorResponse();
        error.setTimestamp(java.time.LocalDateTime.now());
        error.setStatus(HttpStatus.FORBIDDEN.value());
        error.setError("Forbidden");
        error.setMessage(exception.getMessage());
        error.setDetails("You do not have permission to perform this action.");
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
}
