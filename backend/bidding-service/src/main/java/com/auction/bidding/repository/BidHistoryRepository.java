package com.auction.bidding.repository;

import com.auction.bidding.model.BidHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface BidHistoryRepository extends CrudRepository<BidHistory, Long>, PagingAndSortingRepository<BidHistory, Long> {
    Page<BidHistory> findByAuctionId(UUID auctionId, Pageable pageable);
}