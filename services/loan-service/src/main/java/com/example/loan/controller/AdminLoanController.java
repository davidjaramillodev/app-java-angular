package com.example.loan.controller;

import java.util.List;

import com.example.loan.model.DecisionRequest;
import com.example.loan.model.LoanResponse;
import com.example.loan.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/loans")
public class AdminLoanController {
    private final LoanService loanService;

    public AdminLoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<LoanResponse> list() {
        return loanService.findAll();
    }

    @PatchMapping("/{id}/decision")
    public ResponseEntity<LoanResponse> decide(
            @PathVariable Long id,
            @Valid @RequestBody DecisionRequest request) {
        return ResponseEntity.ok(loanService.decide(id, request.status()));
    }
}