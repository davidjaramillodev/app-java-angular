package com.example.loan.service;

public class LoanDecisionConflictException extends RuntimeException {
    public LoanDecisionConflictException() {
        super("La solicitud ya no está pendiente.");
    }
}