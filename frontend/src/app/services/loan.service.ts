import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { LoanApplication, LoanStatus, NewLoanRequest } from '../models/loan.model';

@Injectable({ providedIn: 'root' })
export class LoanService {
  private readonly http = inject(HttpClient);

  myLoans(): Observable<LoanApplication[]> {
    return this.http.get<LoanApplication[]>('/api/v1/loans/my');
  }

  allLoans(): Observable<LoanApplication[]> {
    return this.http.get<LoanApplication[]>('/api/v1/admin/loans');
  }

  apply(request: NewLoanRequest): Observable<LoanApplication> {
    return this.http.post<LoanApplication>('/api/v1/loans', request);
  }

  decide(id: number, status: Extract<LoanStatus, 'APPROVED' | 'REJECTED'>): Observable<LoanApplication> {
    return this.http.patch<LoanApplication>(`/api/v1/admin/loans/${id}/decision`, { status });
  }
}