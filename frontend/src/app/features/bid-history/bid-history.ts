import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { BidService } from '../../core/services/bid.service';
import { BidResponse, Page } from '../../core/models';

@Component({
  selector: 'app-bid-history',
  standalone: true,
  imports: [
    RouterLink, CurrencyPipe, DatePipe,
    MatTableModule, MatPaginatorModule, MatCardModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule,
  ],
  templateUrl: './bid-history.html',
  styleUrl: './bid-history.scss',
})
export class BidHistory implements OnInit {
  auctionId = '';
  bids: BidResponse[] = [];
  displayedColumns = ['amount', 'bidderId', 'timestamp'];
  totalElements = 0;
  pageSize = 10;
  pageIndex = 0;
  loading = true;

  constructor(
    private route: ActivatedRoute,
    private bidService: BidService,
  ) {}

  ngOnInit(): void {
    this.auctionId = this.route.snapshot.paramMap.get('id')!;
    this.loadBids();
  }

  loadBids(): void {
    this.loading = true;
    this.bidService.getBidHistory(this.auctionId, this.pageIndex, this.pageSize).subscribe({
      next: (page: Page<BidResponse>) => {
        this.bids = page.content;
        this.totalElements = page.totalElements;
        this.loading = false;
      },
      error: () => { this.loading = false; },
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadBids();
  }
}
