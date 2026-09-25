package com.example.loan.service;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(Long id) {
        super("Solicitud de préstamo no encontrada: " + id);
    }
}