import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { noAuthGuard } from './core/guards/no-auth.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./features/auth/login/login').then(m => m.Login), canActivate: [noAuthGuard] },
  { path: 'register', loadComponent: () => import('./features/auth/register/register').then(m => m.Register), canActivate: [noAuthGuard] },
  { path: 'auctions', loadComponent: () => import('./features/catalog/catalog').then(m => m.Catalog) },
  { path: 'auctions/new', loadComponent: () => import('./features/create-auction/create-auction').then(m => m.CreateAuction), canActivate: [authGuard] },
  { path: 'auctions/:id/bids', loadComponent: () => import('./features/bid-history/bid-history').then(m => m.BidHistory), canActivate: [authGuard] },
  { path: 'auctions/:id', loadComponent: () => import('./features/auction-detail/auction-detail').then(m => m.AuctionDetail) },
  { path: 'profile', loadComponent: () => import('./features/profile/profile').then(m => m.Profile), canActivate: [authGuard] },
  { path: '', redirectTo: 'auctions', pathMatch: 'full' },
  { path: '**', redirectTo: 'auctions' },
];
