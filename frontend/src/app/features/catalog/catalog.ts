import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuctionService } from '../../core/services/auction.service';
import { AuctionResponse, Page } from '../../core/models';
import { Countdown } from '../../shared/components/countdown/countdown';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [
    RouterLink, CurrencyPipe,
    MatCardModule, MatButtonModule, MatPaginatorModule,
    MatChipsModule, MatIconModule, MatProgressSpinnerModule,
    Countdown,
  ],
  templateUrl: './catalog.html',
  styleUrl: './catalog.scss',
})
export class Catalog implements OnInit {
  auctions: AuctionResponse[] = [];
  totalElements = 0;
  pageSize = 12;
  pageIndex = 0;
  loading = true;

  constructor(private auctionService: AuctionService) {}

  ngOnInit(): void {
    this.loadAuctions();
  }

  loadAuctions(): void {
    this.loading = true;
    this.auctionService.getAuctions(this.pageIndex, this.pageSize).subscribe({
      next: (page: Page<AuctionResponse>) => {
        this.auctions = page.content;
        this.totalElements = page.totalElements;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      },
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadAuctions();
  }
}
