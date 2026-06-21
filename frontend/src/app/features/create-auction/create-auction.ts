import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuctionService } from '../../core/services/auction.service';
import { AuctionCreateRequest, ApiError } from '../../core/models';

@Component({
  selector: 'app-create-auction',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatDatepickerModule, MatNativeDateModule,
    MatSnackBarModule, MatProgressSpinnerModule,
  ],
  templateUrl: './create-auction.html',
  styleUrl: './create-auction.scss',
})
export class CreateAuction {
  form: FormGroup;
  loading = false;
  minDate = new Date();

  constructor(
    private fb: FormBuilder,
    private auctionService: AuctionService,
    private router: Router,
    private snackBar: MatSnackBar,
  ) {
    this.form = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.maxLength(2000)]],
      startingPrice: [null, [Validators.required, Validators.min(0.01)]],
      endDate: [null, Validators.required],
      endTime: ['18:00', Validators.required],
    });
  }

  onSubmit(): void {
    if (this.form.invalid) return;
    this.loading = true;

    const { title, description, startingPrice, endDate, endTime } = this.form.value;
    const [hours, minutes] = (endTime as string).split(':').map(Number);
    const end = new Date(endDate);
    end.setHours(hours, minutes, 0, 0);

    const request: AuctionCreateRequest = {
      title,
      description,
      startingPrice,
      endTimestamp: end.toISOString().replace('Z', ''),
    };

    this.auctionService.createAuction(request).subscribe({
      next: (auction) => {
        this.loading = false;
        this.snackBar.open('Auction created!', 'Close', { duration: 3000 });
        this.router.navigate(['/auctions', auction.id]);
      },
      error: (err) => {
        this.loading = false;
        const apiError = err.error as ApiError;
        this.snackBar.open(apiError?.message ?? 'Failed to create auction', 'Close', { duration: 4000 });
      },
    });
  }
}
