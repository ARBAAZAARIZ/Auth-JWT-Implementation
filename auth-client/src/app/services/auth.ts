import { inject, Injectable } from '@angular/core';


// --- API URL Defined Directly ---
const API_BASE_URL = 'http://localhost:8080';

import {
  LoginRequest,
  RegisterRequest,
  LoginResponse
} from '../interfaces/auth.interface';
import { catchError, Observable, of, tap } from 'rxjs';
import { HttpClient, HttpHandler, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router'; // <-- 2. Need this specific import for Router
import { response } from 'express';

// Define keys for localStorage
const ACCESS_TOKEN_KEY = 'auth_access_token';
const REFRESH_TOKEN_KEY = 'auth_refresh_token';

@Injectable({
  providedIn: 'root',
})
export class Auth {

  private http = inject(HttpClient);
  private router = inject(Router); // <-- 1. Inject Router

  // -----core api calls-----

  register(request: RegisterRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_BASE_URL}/api/auth/register`, request)
      .pipe(
        tap(response => this.saveTokens(response)),
        tap(() => this.router.navigate(['/home'])
        )
      );
  }

  saveTokens(response: LoginResponse): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, response.accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, response.refreshToken);
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_BASE_URL}/api/auth/login`, request)
      .pipe(
        tap(response => this.saveTokens(response)),
        tap(() => this.router.navigate(['/home']))
      );
  }

  logout(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    this.router.navigate(['/login']);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getAccessToken(); // Used by AuthGuard
  }

  refresh(): Observable<LoginResponse | null> {
    const refreshToken = this.getRefreshToken();

    if (!refreshToken) {
      this.logout();
      return of(null);
    }
// 1. Define the body object (matching RefreshTokenRequest.java)
    const body = { refreshToken: refreshToken };

   return this.http.post<LoginResponse>(`${API_BASE_URL}/api/auth/refresh`, body) // <-- CHANGED
      .pipe(
        tap(response => this.saveTokens(response)),
        catchError((error) => {
          this.logout();
          return of(null); 
        })
      );

  }

}
