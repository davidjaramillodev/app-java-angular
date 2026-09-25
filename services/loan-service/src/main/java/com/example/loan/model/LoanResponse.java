package com.example.loan.model;

import java.math.BigDecimal;
import java.time.Instant;

public record LoanResponse(
        Long id,
        String applicantEmail,
        BigDecimal amount,
        Integer termMonths,
        String purpose,
        LoanStatus status,
        Instant createdAt) {
    public static LoanResponse from(Loan loan) {
        return new LoanResponse(loan.getId(), loan.getApplicantEmail(), loan.getAmount(),
                loan.getTermMonths(), loan.getPurpose(), loan.getStatus(), loan.getCreatedAt());
    }
}