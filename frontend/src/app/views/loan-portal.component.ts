import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { LoanApplication, LoanStatus } from '../models/loan.model';
import { AuthService } from '../services/auth.service';
import { LoanService } from '../services/loan.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, ReactiveFormsModule],
  templateUrl: './loan-portal.component.html',
  styleUrl: './loan-portal.component.css'
})
export class LoanPortalComponent {
  readonly auth = inject(AuthService);
  private readonly loanService = inject(LoanService);
  readonly loans = signal<LoanApplication[]>([]);
  readonly loansLoading = signal(false);
  readonly loansError = signal(false);
  readonly loginBusy = signal(false);
  readonly loginError = signal(false);
  readonly applyBusy = signal(false);
  readonly applyError = signal(false);
  readonly decisionBusy = signal<number | null>(null);
  readonly decisionError = signal(false);
  readonly loginForm = new FormGroup({
    username: new FormControl('usuario@test.com', {
      nonNullable: true,
      validators: [Validators.required, Validators.email]
    }),
    password: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required]
    })
  });
  readonly loanForm = new FormGroup({
    amount: new FormControl(5000, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(100)]
    }),
    termMonths: new FormControl(24, {
      nonNullable: true,
      validators: [Validators.required, Validators.min(3), Validators.max(360)]
    }),
    purpose: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(300)]
    })
  });

  pendingCount(): number {
    return this.loans().filter(loan => loan.status === 'PENDING').length;
  }

  statusLabel(status: LoanStatus): string {
    return status === 'PENDING' ? 'Pendiente' : status === 'APPROVED' ? 'Aprobado' : 'Rechazado';
  }

  login(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const { username, password } = this.loginForm.getRawValue();
    this.loginError.set(false);
    this.loginBusy.set(true);
    this.auth.login({ username, password }).pipe(
      finalize(() => this.loginBusy.set(false))
    ).subscribe({
      next: () => this.loadLoans(),
      error: () => this.loginError.set(true)
    });
  }

  loadLoans(): void {
    const request = this.auth.role() === 'ADMIN'
      ? this.loanService.allLoans()
      : this.loanService.myLoans();
    this.loansLoading.set(true);
    this.loansError.set(false);
    request.pipe(
      finalize(() => this.loansLoading.set(false))
    ).subscribe({
      next: loans => this.loans.set(loans),
      error: () => this.loansError.set(true)
    });
  }

  applyLoan(): void {
    if (this.loanForm.invalid) {
      this.loanForm.markAllAsTouched();
      return;
    }

    this.applyError.set(false);
    this.applyBusy.set(true);
    this.loanService.apply(this.loanForm.getRawValue()).pipe(
      finalize(() => this.applyBusy.set(false))
    ).subscribe({
      next: () => {
        this.loanForm.controls.purpose.reset('');
        this.loadLoans();
      },
      error: () => this.applyError.set(true)
    });
  }

  decide(loan: LoanApplication, status: 'APPROVED' | 'REJECTED'): void {
    this.decisionError.set(false);
    this.decisionBusy.set(loan.id);
    this.loanService.decide(loan.id, status).pipe(
      finalize(() => this.decisionBusy.set(null))
    ).subscribe({
      next: () => this.loadLoans(),
      error: () => this.decisionError.set(true)
    });
  }

  logout(): void {
    this.auth.logout();
    this.loans.set([]);
    this.loginForm.controls.password.reset();
  }
}