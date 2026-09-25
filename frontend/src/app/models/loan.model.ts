export type LoanStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface LoanApplication {
  id: number;
  applicantEmail: string;
  amount: number;
  termMonths: number;
  purpose: string;
  status: LoanStatus;
  createdAt: string;
}

export interface NewLoanRequest {
  amount: number;
  termMonths: number;
  purpose: string;
}