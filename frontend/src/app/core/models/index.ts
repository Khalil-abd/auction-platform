export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RefreshRequest {
  refreshToken: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
}

export interface RegisterResponse {
  userId: string;
}

export type AuctionStatus = 'DRAFT' | 'ACTIVE' | 'SUSPENDED' | 'CONCLUDED' | 'SETTLED';

export interface AuctionCreateRequest {
  title: string;
  description: string;
  startingPrice: number;
  endTimestamp: string;
  dynamicAttributes?: Record<string, unknown>;
}

export interface AuctionResponse {
  id: string;
  title: string;
  description: string;
  sellerId: string;
  startingPrice: number;
  endTimestamp: string;
  status: AuctionStatus;
  dynamicAttributes?: Record<string, unknown>;
}

export interface PlaceBidRequest {
  auctionId: string;
  amount: number;
}

export interface BidResponse {
  id: number;
  auctionId: string;
  bidderId: string;
  amount: number;
  timestamp: string;
}

export interface BidPlacedEvent {
  auctionId: string;
  bidderId: string;
  amount: number;
  timestamp: string;
}

export interface ApiError {
  errorCode: string;
  message: string;
  status: number;
  timestamp: string;
  path: string;
  details?: string[];
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  last: boolean;
  first: boolean;
  size: number;
  number: number;
  numberOfElements: number;
  empty: boolean;
}
