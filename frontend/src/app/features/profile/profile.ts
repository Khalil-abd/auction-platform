import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../core/services/auth.service';
import { AuctionService } from '../../core/services/auction.service';
import { AuctionResponse } from '../../core/models';
import { Countdown } from '../../shared/components/countdown/countdown';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    RouterLink, CurrencyPipe,
    MatCardModule, MatButtonModule, MatIconModule,
    MatListModule, MatDividerModule, MatProgressSpinnerModule,
    Countdown,
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile implements OnInit {
  userId: string | null = null;
  roles = '';
  myAuctions: AuctionResponse[] = [];
  loadingAuctions = true;

  constructor(
    private authService: AuthService,
    private auctionService: AuctionService,
  ) {}

  ngOnInit(): void {
    this.userId = this.authService.getUserId();
    this.roles = this.authService.getUserRoles();

    this.auctionService.getAuctions(0, 100).subscribe({
      next: (page) => {
        this.myAuctions = page.content.filter(a => a.sellerId === this.userId);
        this.loadingAuctions = false;
      },
      error: () => { this.loadingAuctions = false; },
    });
  }

  logout(): void {
    this.authService.logout();
  }
}
