package com.example.loan.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.example.loan.model.CreateLoanRequest;
import com.example.loan.model.Loan;
import com.example.loan.model.LoanResponse;
import com.example.loan.model.LoanStatus;
import com.example.loan.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {
    @Mock
    private LoanRepository loans;

    private LoanService loanService;

    @BeforeEach
    void setUp() {
        loanService = new LoanService(loans);
    }

    @Test
    void findForApplicantQueriesOnlyThatApplicantsLoans() {
        Loan loan = pendingLoan("usuario@test.com");
        when(loans.findByApplicantEmailOrderByCreatedAtDesc("usuario@test.com"))
                .thenReturn(List.of(loan));

        List<LoanResponse> result = loanService.findForApplicant("usuario@test.com");

        assertEquals(1, result.size());
        assertEquals("usuario@test.com", result.getFirst().applicantEmail());
        verify(loans).findByApplicantEmailOrderByCreatedAtDesc("usuario@test.com");
    }

    @Test
    void createTrimsPurposeAndStoresLoanForAuthenticatedApplicant() {
        when(loans.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CreateLoanRequest request = new CreateLoanRequest(
                new BigDecimal("7500.00"), 36, "  Compra de vivienda  ");

        LoanResponse result = loanService.create(request, "usuario@test.com");

        ArgumentCaptor<Loan> loanCaptor = ArgumentCaptor.forClass(Loan.class);
        verify(loans).save(loanCaptor.capture());
        Loan savedLoan = loanCaptor.getValue();
        assertEquals("usuario@test.com", savedLoan.getApplicantEmail());
        assertEquals(new BigDecimal("7500.00"), savedLoan.getAmount());
        assertEquals(36, savedLoan.getTermMonths());
        assertEquals("Compra de vivienda", savedLoan.getPurpose());
        assertEquals(LoanStatus.PENDING, savedLoan.getStatus());
        assertEquals(LoanStatus.PENDING, result.status());
    }

    @Test
    void findAllReturnsEveryLoanInRepositoryOrder() {
        when(loans.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(pendingLoan("usuario@test.com")));

        List<LoanResponse> result = loanService.findAll();

        assertEquals(1, result.size());
        verify(loans).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void decideUpdatesPendingLoan() {
        Loan loan = pendingLoan("usuario@test.com");
        when(loans.findById(1L)).thenReturn(Optional.of(loan));
        when(loans.save(loan)).thenReturn(loan);

        LoanResponse result = loanService.decide(1L, LoanStatus.APPROVED);

        assertEquals(LoanStatus.APPROVED, result.status());
        verify(loans).save(loan);
    }

    @Test
    void decideRejectsAlreadyProcessedLoan() {
        Loan loan = pendingLoan("usuario@test.com");
        loan.decide(LoanStatus.APPROVED);
        when(loans.findById(1L)).thenReturn(Optional.of(loan));

        assertThrows(LoanDecisionConflictException.class,
                () -> loanService.decide(1L, LoanStatus.REJECTED));
        verify(loans, never()).save(any(Loan.class));
    }

    @Test
    void decideRejectsPendingAsFinalDecision() {
        when(loans.findById(1L)).thenReturn(Optional.of(pendingLoan("usuario@test.com")));

        assertThrows(LoanDecisionConflictException.class,
                () -> loanService.decide(1L, LoanStatus.PENDING));
        verify(loans, never()).save(any(Loan.class));
    }

    @Test
    void decideReportsMissingLoan() {
        when(loans.findById(404L)).thenReturn(Optional.empty());

        assertThrows(LoanNotFoundException.class,
                () -> loanService.decide(404L, LoanStatus.APPROVED));
        verify(loans, never()).save(any(Loan.class));
    }

    private Loan pendingLoan(String email) {
        return new Loan(email, new BigDecimal("5000.00"), 24, "Reforma");
    }
}