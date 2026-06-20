package com.auction.bidding.repository;

import com.auction.bidding.model.AuctionEngine;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;

public interface AuctionEngineRepository extends CrudRepository<AuctionEngine, UUID> {

    // Pessimistic Locking (SELECT ... FOR UPDATE) to prevent race conditions during high-speed bidding matches
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AuctionEngine a WHERE a.auctionId = :id")
    Optional<AuctionEngine> findByIdForUpdate(@Param("id") UUID id);
}