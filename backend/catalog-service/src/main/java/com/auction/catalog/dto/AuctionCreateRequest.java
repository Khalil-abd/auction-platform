package com.auction.catalog.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class AuctionCreateRequest {
    private String title;
    private String description;
    private String sellerId;
    private BigDecimal startingPrice;
    private LocalDateTime endTimestamp;
    private Map<String, Object> dynamicAttributes;
}