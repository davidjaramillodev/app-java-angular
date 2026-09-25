package com.example.loan.service;

import java.util.List;

import com.example.loan.model.CreateLoanRequest;
import com.example.loan.model.Loan;
import com.example.loan.model.LoanResponse;
import com.example.loan.model.LoanStatus;
import com.example.loan.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanService {
    private final LoanRepository loans;

    public LoanService(LoanRepository loans) {
        this.loans = loans;
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> findForApplicant(String email) {
        return loans.findByApplicantEmailOrderByCreatedAtDesc(email).stream()
                .map(LoanResponse::from)
                .toList();
    }

    @Transactional
    public LoanResponse create(CreateLoanRequest request, String applicantEmail) {
        Loan loan = new Loan(applicantEmail, request.amount(), request.termMonths(), request.purpose().trim());
        return LoanResponse.from(loans.save(loan));
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> findAll() {
        return loans.findAllByOrderByCreatedAtDesc().stream()
                .map(LoanResponse::from)
                .toList();
    }

    @Transactional
    public LoanResponse decide(Long id, LoanStatus status) {
        Loan loan = loans.findById(id).orElseThrow(() -> new LoanNotFoundException(id));
        if (loan.getStatus() != LoanStatus.PENDING || status == LoanStatus.PENDING) {
            throw new LoanDecisionConflictException();
        }
        loan.decide(status);
        return LoanResponse.from(loans.save(loan));
    }
}