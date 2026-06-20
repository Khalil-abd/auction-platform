package com.auction.notification.event;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BidPlacedEvent {
    private UUID auctionId;
    private UUID bidderId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}