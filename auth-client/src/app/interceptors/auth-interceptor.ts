import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';

import { catchError, Observable, switchMap, throwError, tap } from 'rxjs';
import { Auth } from '../services/auth';

// Use a flag to prevent multiple refresh requests from happening at once
let isRefreshing = false;

/**
 * The functional interceptor responsible for:
 * 1. Attaching the Access Token to every outgoing request.
 * 2. Catching 401 errors and refreshing the token using the Refresh Token.
 */
export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(Auth);

  // 1. ATTACH THE TOKEN (if available)
  const accessToken = authService.getAccessToken();
  if (accessToken) {
    req = addToken(req, accessToken);
  }

  // 2. CATCH ERRORS AND HANDLE REFRESH
  // We use the 'handle' method to delegate the request and then listen for the response/error
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // We only care about 401 Unauthorized errors
      if (error.status === 401) {
        return handle401Error(req, next, authService);
      }
      // For all other errors (400, 403, 500, etc.), just re-throw them.
      return throwError(() => error);
    })
  );
};

// --- Helper Functions ---

/**
 * Clones the request and adds the Authorization header with the Bearer token.
 */
function addToken(request: HttpRequest<unknown>, token: string) {
  return request.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });
}

/**
 * Handles the 401 error by checking for refresh status and initiating the refresh sequence.
 * This is the core of our secure token rotation logic.
 */
function handle401Error(req: HttpRequest<unknown>, next: HttpHandlerFn, authService: Auth): Observable<HttpEvent<unknown>> {
  
  // CRITICAL CHECK: If we are already refreshing, we wait for that process to finish.
  if (isRefreshing) {
    // In a production app, you would queue the request here and retry later.
    // For now, we will just force a simple retry after the refresh completes.
    console.log("Token refresh already in progress. Retrying request...");
    return throwError(() => new Error('Refresh in progress')); // Simplification for now
  }

  // Set the flag to true to prevent concurrent refresh attempts
  isRefreshing = true;
  console.log("401 caught. Attempting token refresh...");

  // Call the AuthService to perform the refresh API call
  return authService.refresh().pipe(
    switchMap((response) => {
      isRefreshing = false; // Refresh process finished
      
      if (response && response.accessToken) {
        // SUCCESS: Tokens renewed. Retry the original failed request
        console.log("Tokens successfully refreshed. Retrying original request...");
        return next(addToken(req, response.accessToken));
      } else {
        // FAILURE: Refresh token was invalid/expired. The AuthService.refresh() 
        // already called authService.logout() (the "kill switch").
        return throwError(() => new HttpErrorResponse({ status: 401, error: { error: 'Session expired. Please log in again.' } }));
      }
    }),
    catchError((error) => {
      // Final catch for any refresh network/server error
      isRefreshing = false;
      authService.logout();
      return throwError(() => error);
    })
  );
}