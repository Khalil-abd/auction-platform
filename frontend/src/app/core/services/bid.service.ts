import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { BidResponse, Page, PlaceBidRequest } from '../models';

@Injectable({ providedIn: 'root' })
export class BidService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/bids`;

  constructor(private http: HttpClient) {}

  placeBid(auctionId: string, amount: number): Observable<BidResponse> {
    const body: PlaceBidRequest = { auctionId, amount };
    return this.http.post<BidResponse>(this.apiUrl, body);
  }

  getBidHistory(auctionId: string, page = 0, size = 10): Observable<Page<BidResponse>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get<Page<BidResponse>>(`${this.apiUrl}/auction/${auctionId}`, { params });
  }
}
