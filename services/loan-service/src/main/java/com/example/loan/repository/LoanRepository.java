package com.example.loan.repository;

import java.util.List;

import com.example.loan.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByApplicantEmailOrderByCreatedAtDesc(String applicantEmail);
    List<Loan> findAllByOrderByCreatedAtDesc();
}