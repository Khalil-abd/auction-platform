package com.auction.bidding.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PlaceBidRequest {
    private UUID auctionId;
    private UUID bidderId;
    private BigDecimal amount;
}