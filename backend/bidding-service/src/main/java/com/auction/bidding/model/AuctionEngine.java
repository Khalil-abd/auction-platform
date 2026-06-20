package com.auction.bidding.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "current_auctions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionEngine {

    @Id
    private UUID auctionId;

    @Column(nullable = false)
    private BigDecimal currentHighestBid;

    private UUID highestBidderId;

    @Version // Optimistic locking mechanism safeguarding state transitions
    private Long version;
}