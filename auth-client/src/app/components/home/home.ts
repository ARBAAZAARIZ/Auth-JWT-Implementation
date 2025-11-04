import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { Auth } from '../../services/auth';
import { HttpClient } from '@angular/common/http';


const API_BASE_URL = 'http://localhost:8080';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule,MatButtonModule,MatCardModule],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {

  authservice = inject(Auth);

  http = inject(HttpClient);
 

  // Data to display
  secureMessage: string = 'Loading secure data...';
  userEmail: string | null = null;

  ngOnInit(): void {
    // Attempt to fetch the secure data when the component loads
    this.fetchSecureData();
    this.userEmail = localStorage.getItem('auth_access_token'); // Simplified way to show token exists
  }


  fetchSecureData(): void {
    // This call will be intercepted by AuthInterceptor, which adds the token.
    this.http.get(`${API_BASE_URL}/api/v1/demo`, { responseType: 'text' }).subscribe({
      next: (response) => {
        this.secureMessage = response; // Expected: "Hello, arbaaz! Your token is valid..."
      },
      error: (err) => {
        this.secureMessage = 'Error fetching data: Session may be expired.';
        console.error('Error fetching secure data:', err);
      }
    });
  }


}
