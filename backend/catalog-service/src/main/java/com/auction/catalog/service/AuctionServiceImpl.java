package com.auction.catalog.service;

import com.auction.catalog.model.AuctionItem;
import com.auction.catalog.model.AuctionStatus;
import com.auction.catalog.repository.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository repository;

    @Override
    public AuctionItem createAuction(AuctionItem item) {
        log.info("Processing business rules for creating auction: {}", item.getTitle());
        item.setStatus(AuctionStatus.ACTIVE);
        return repository.save(item);
    }

    @Override
    public Page<AuctionItem> getActiveAuctions(Pageable pageable) {
        log.debug("Fetching page of active auctions");
        return repository.findByStatus(AuctionStatus.ACTIVE, pageable);
    }

    @Override
    public Optional<AuctionItem> getAuctionById(String id) {
        log.debug("Fetching auction by id: {}", id);
        return repository.findById(id);
    }
}