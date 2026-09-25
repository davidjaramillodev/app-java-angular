import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { AccountRole, LoginRequest, LoginResponse } from '../models/account.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  readonly accessToken = signal<string | null>(null);
  readonly username = signal<string | null>(null);
  readonly role = signal<AccountRole | null>(null);

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/v1/auth/login', request).pipe(
      tap(response => {
        this.accessToken.set(response.accessToken);
        this.username.set(response.username);
        this.role.set(response.role);
      })
    );
  }

  logout(): void {
    this.accessToken.set(null);
    this.username.set(null);
    this.role.set(null);
  }
}