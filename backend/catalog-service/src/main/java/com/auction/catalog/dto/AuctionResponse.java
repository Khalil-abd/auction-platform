package com.auction.catalog.dto;

import com.auction.catalog.model.AuctionStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class AuctionResponse {
    private String id;
    private String title;
    private String description;
    private String sellerId;
    private BigDecimal startingPrice;
    private LocalDateTime endTimestamp;
    private AuctionStatus status;
    private Map<String, Object> dynamicAttributes;
}