import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { authGuard } from './guards/auth-guard';
import { Login } from './components/login/login';
import { Register } from './components/register/register';

export const routes: Routes = [
    {
        path:'home', component: Home, canActivate:[authGuard] // <-- Protected by our Guard
    },
    // Public Routes
    {
        path: 'login', component:Login
    },
    {
        path: 'register', component:Register
    },
    {
        path: '', redirectTo: '/login', pathMatch: 'full'
    },
    { 
        path: '**', redirectTo: '/login'
    }
];
