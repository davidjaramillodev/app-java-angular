package com.example.loan.config;

import java.math.BigDecimal;

import com.example.loan.model.Loan;
import com.example.loan.repository.LoanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoLoanSeeder {
    @Bean
    CommandLineRunner seedLoanApplications(LoanRepository loans) {
        return args -> {
            if (loans.count() == 0) {
                loans.save(new Loan("usuario@test.com", new BigDecimal("5000.00"), 24,
                        "Remodelación del hogar"));
            }
        };
    }
}