import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router'; 
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';


// --- IMPORT ANGULAR MATERIAL MODULES ---
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Auth } from '../../services/auth';
@Component({
  selector: 'app-login',
  imports: [CommonModule,
    ReactiveFormsModule,
    RouterLink, 
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  loginForm: FormGroup;
  authService = inject(Auth);
  fb = inject(FormBuilder);
  snackBar = inject(MatSnackBar);

  constructor() {
    this.loginForm = this.fb.group({
      // We are matching the backend DTO property name: usernameOrEmail
      usernameOrEmail: ['', [Validators.required, Validators.email]], 
      password: ['', [Validators.required]]
    });
  }


  onSubmit(): void {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: (response) => {
          // Tokens saved and user navigated to /home inside AuthService
          this.snackBar.open('Login Successful!', 'Close', { duration: 3000 });
        },
        error: (err) => {
          // Display error message from the backend's GlobalExceptionHandler (e.g., 401 Unauthorized)
          // We use err.error?.error because our backend sends a JSON object: { "error": "Invalid username or password" }
          const errorMessage = err.error?.error || 'Login failed. Please check your credentials.';
          this.snackBar.open(errorMessage, 'Close', { duration: 5000, panelClass: ['error-snackbar'] });
          console.error('Login failed:', err);
        }
      });
    }
  }

}
