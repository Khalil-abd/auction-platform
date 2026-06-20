package com.auction.catalog.repository;

import com.auction.catalog.model.AuctionItem;
import com.auction.catalog.model.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRepository extends CrudRepository<AuctionItem, String>, PagingAndSortingRepository<AuctionItem, String> {
    // Spring Data automatically handles binding the Enum value to the MongoDB query
    Page<AuctionItem> findByStatus(AuctionStatus status, Pageable pageable);
}