import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { canActivate } from './gaurds/auth.gaurd';

export const routes: Routes = [
  { path: '', component: Home, canActivate: [canActivate] },
];
