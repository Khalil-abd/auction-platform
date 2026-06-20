package com.auction.bidding.service;

import com.auction.bidding.model.BidHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.UUID;

public interface BiddingService {
    BidHistory placeBid(UUID auctionId, UUID bidderId, BigDecimal amount);
    Page<BidHistory> getBidHistory(UUID auctionId, Pageable pageable);
}