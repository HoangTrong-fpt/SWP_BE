package com.quitsmoking.platform.exception.handler;

import com.quitsmoking.platform.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;


@RestControllerAdvice
public class AuthenExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthenExceptionHandler.class);

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, String details) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMessage(message);
        error.setDetails(details);
        return error;
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateException(SQLIntegrityConstraintViolationException exception){
        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                exception.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException exception){
        ErrorResponse error = buildErrorResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                exception.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException exception){
        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                exception.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}