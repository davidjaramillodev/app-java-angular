package com.example.loan.controller;

import java.util.List;

import com.example.loan.model.CreateLoanRequest;
import com.example.loan.model.LoanResponse;
import com.example.loan.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/my")
    public List<LoanResponse> mine(@AuthenticationPrincipal Jwt jwt) {
        return loanService.findForApplicant(jwt.getSubject());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse apply(
            @Valid @RequestBody CreateLoanRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return loanService.create(request, jwt.getSubject());
    }
}