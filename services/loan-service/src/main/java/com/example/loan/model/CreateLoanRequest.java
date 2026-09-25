package com.example.loan.model;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateLoanRequest(
        @NotNull @DecimalMin("100.00") @Digits(integer = 10, fraction = 2) BigDecimal amount,
        @NotNull @Min(3) @Max(360) Integer termMonths,
        @NotBlank @Size(max = 300) String purpose) {
}