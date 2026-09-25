package com.example.loan.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "loan_applications")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 254)
    private String applicantEmail;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer termMonths;

    @Column(nullable = false, length = 300)
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private LoanStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    protected Loan() {
    }

    public Loan(String applicantEmail, BigDecimal amount, Integer termMonths, String purpose) {
        this.applicantEmail = applicantEmail;
        this.amount = amount;
        this.termMonths = termMonths;
        this.purpose = purpose;
        this.status = LoanStatus.PENDING;
    }

    @PrePersist
    void setCreatedAt() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public String getPurpose() {
        return purpose;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void decide(LoanStatus status) {
        this.status = status;
    }
}