package com.auction.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "auctions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionItem {
    @Id
    private String id;
    private String title;
    private String description;
    private String sellerId;
    private BigDecimal startingPrice;
    private LocalDateTime endTimestamp;
    private AuctionStatus status;
    private Map<String, Object> dynamicAttributes;
}
