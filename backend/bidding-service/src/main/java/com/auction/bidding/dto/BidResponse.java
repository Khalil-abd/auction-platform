package com.auction.bidding.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BidResponse {
    private Long id;
    private String auctionId;
    private String bidderId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
}