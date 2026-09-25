package com.example.loan.model;

import jakarta.validation.constraints.NotNull;

public record DecisionRequest(@NotNull LoanStatus status) {
}