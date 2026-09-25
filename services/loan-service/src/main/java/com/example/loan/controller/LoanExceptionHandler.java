package com.example.loan.controller;

import com.example.loan.service.LoanDecisionConflictException;
import com.example.loan.service.LoanNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LoanExceptionHandler {
    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<ApiError> notFound(LoanNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(exception.getMessage()));
    }

    @ExceptionHandler(LoanDecisionConflictException.class)
    public ResponseEntity<ApiError> conflict(LoanDecisionConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(exception.getMessage()));
    }

    public record ApiError(String message) {
    }
}