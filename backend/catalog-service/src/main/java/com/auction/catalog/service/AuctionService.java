package com.auction.catalog.service;

import com.auction.catalog.model.AuctionItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface AuctionService {
    AuctionItem createAuction(AuctionItem item);
    Page<AuctionItem> getActiveAuctions(Pageable pageable);
    Optional<AuctionItem> getAuctionById(String id);
}