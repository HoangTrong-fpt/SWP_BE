package com.quitsmoking.platform.quitsmoking.exception.handler;

import com.quitsmoking.platform.quitsmoking.exception.exceptions.AuthorizeException;
import com.quitsmoking.platform.quitsmoking.exception.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
public class APIHandleException {
    // Mỗi khi có lỗi validation thì chạy xử lí này
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleBadRequestException(MethodArgumentNotValidException exception){
        String messages = "";

        for(FieldError error: exception.getBindingResult().getFieldErrors()){
            messages += error.getDefaultMessage() + "\n";
        }

        return new ResponseEntity(messages, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity handleDuplicate(SQLIntegrityConstraintViolationException exception){
        return new ResponseEntity("Duplicate", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity handleNullPointer(NullPointerException exception){
        return new ResponseEntity(exception.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizeException.class)
    public ResponseEntity handleAuthenticateException(AuthorizeException exception){
        return new ResponseEntity(exception.getMessage(),HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity handleRuntimeException(RuntimeException exception){
        return new ResponseEntity(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException(NotFoundException exception){
        return new ResponseEntity(exception.getMessage(),HttpStatus.NOT_FOUND);
    }
}
