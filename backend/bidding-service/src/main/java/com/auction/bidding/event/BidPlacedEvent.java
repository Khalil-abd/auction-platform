package com.auction.bidding.event;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BidPlacedEvent {
    private UUID auctionId;
    private UUID bidderId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}