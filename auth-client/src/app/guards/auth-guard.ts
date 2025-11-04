import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '../services/auth';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {

  const authService = inject(Auth);
  const rourter = inject(Router);

  if(authService.isLoggedIn()){
    return true;
  }else{
    rourter.navigate(['/login']);
    return false;
  }
};
