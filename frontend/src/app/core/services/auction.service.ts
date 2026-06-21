import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuctionCreateRequest, AuctionResponse, Page } from '../models';

@Injectable({ providedIn: 'root' })
export class AuctionService {
  private readonly apiUrl = `${environment.apiUrl}/api/v1/auctions`;

  constructor(private http: HttpClient) {}

  getAuctions(page = 0, size = 12, sortBy = 'id', direction = 'DESC'): Observable<Page<AuctionResponse>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sortBy', sortBy)
      .set('direction', direction);
    return this.http.get<Page<AuctionResponse>>(this.apiUrl, { params });
  }

  getAuctionById(id: string): Observable<AuctionResponse> {
    return this.http.get<AuctionResponse>(`${this.apiUrl}/${id}`);
  }

  createAuction(request: AuctionCreateRequest): Observable<AuctionResponse> {
    return this.http.post<AuctionResponse>(this.apiUrl, request);
  }
}
