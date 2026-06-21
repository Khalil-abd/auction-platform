import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDividerModule } from '@angular/material/divider';
import { AuctionService } from '../../core/services/auction.service';
import { BidService } from '../../core/services/bid.service';
import { WebSocketService } from '../../core/services/websocket.service';
import { AuthService } from '../../core/services/auth.service';
import { AuctionResponse, BidPlacedEvent, ApiError } from '../../core/models';
import { Countdown } from '../../shared/components/countdown/countdown';

@Component({
  selector: 'app-auction-detail',
  standalone: true,
  imports: [
    RouterLink, CurrencyPipe, DatePipe, ReactiveFormsModule,
    MatCardModule, MatButtonModule, MatFormFieldModule, MatInputModule,
    MatListModule, MatChipsModule, MatIconModule, MatSnackBarModule,
    MatProgressSpinnerModule, MatDividerModule,
    Countdown,
  ],
  templateUrl: './auction-detail.html',
  styleUrl: './auction-detail.scss',
})
export class AuctionDetail implements OnInit, OnDestroy {
  auction: AuctionResponse | null = null;
  liveBids: BidPlacedEvent[] = [];
  bidForm: FormGroup;
  loading = true;
  submitting = false;
  private wsSub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private auctionService: AuctionService,
    private bidService: BidService,
    private wsService: WebSocketService,
    protected authService: AuthService,
    private snackBar: MatSnackBar,
    fb: FormBuilder,
  ) {
    this.bidForm = fb.group({
      amount: [null, [Validators.required, Validators.min(0.01)]],
    });
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.auctionService.getAuctionById(id).subscribe({
      next: (auction) => {
        this.auction = auction;
        this.loading = false;
      },
      error: () => { this.loading = false; },
    });

    this.wsSub = this.wsService.subscribeToBids(id).subscribe((event) => {
      this.liveBids = [event, ...this.liveBids].slice(0, 50);
    });
  }

  ngOnDestroy(): void {
    this.wsSub?.unsubscribe();
  }

  placeBid(): void {
    if (this.bidForm.invalid || !this.auction) return;
    this.submitting = true;

    this.bidService.placeBid(this.auction.id, this.bidForm.value.amount).subscribe({
      next: () => {
        this.submitting = false;
        this.bidForm.reset();
        this.snackBar.open('Bid placed successfully!', 'Close', { duration: 3000 });
      },
      error: (err) => {
        this.submitting = false;
        const apiError = err.error as ApiError;
        this.snackBar.open(apiError?.message ?? 'Failed to place bid', 'Close', { duration: 4000 });
      },
    });
  }
}
