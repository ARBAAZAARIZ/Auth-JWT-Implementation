import { ApplicationConfig, importProvidersFrom, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { AuthInterceptor } from './interceptors/auth-interceptor';




export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    // 1. For Angular Material animations

    // 2. For making API calls
    provideHttpClient(withInterceptors([AuthInterceptor])),
    // 3. For building forms (this is the correct way)
    importProvidersFrom(ReactiveFormsModule),
    provideRouter(routes), provideClientHydration(withEventReplay())
  ]
};
