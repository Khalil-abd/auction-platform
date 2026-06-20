package com.auction.bidding.service;

import com.auction.bidding.model.AuctionEngine;
import com.auction.bidding.model.BidHistory;
import com.auction.bidding.repository.AuctionEngineRepository;
import com.auction.bidding.repository.BidHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BiddingServiceImpl implements BiddingService {

    private final AuctionEngineRepository engineRepository;
    private final BidHistoryRepository historyRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String REDIS_PREFIX = "auction:highest_bid:";

    @Override
    @Transactional
    public BidHistory placeBid(UUID auctionId, UUID bidderId, BigDecimal amount) {
        String redisKey = REDIS_PREFIX + auctionId;

        // 1. Redis fast-abort pre-validation
        validateAgainstCache(redisKey, amount);

        // 2. Obtain database lock and perform ground-truth transactional validation
        AuctionEngine engine = getOrCreateAuctionEngine(auctionId);
        validateBidAmount(amount, engine.getCurrentHighestBid());

        // 3. Mutate persistent layers
        updateAuctionState(engine, amount, bidderId);
        BidHistory savedHistory = recordBidHistory(auctionId, bidderId, amount);

        // 4. Update the ephemeral distributed cache
        updateCache(redisKey, amount);

        log.info("Successfully accepted bid of {} on auction {} by user {}", amount, auctionId, bidderId);
        return savedHistory;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BidHistory> getBidHistory(UUID auctionId, Pageable pageable) {
        return historyRepository.findByAuctionId(auctionId, pageable);
    }

    // --- Private Helper Methods enforcing SRP ---

    private void validateAgainstCache(String redisKey, BigDecimal amount) {
        String cachedHighest = redisTemplate.opsForValue().get(redisKey);
        if (cachedHighest != null) {
            BigDecimal currentCachedBid = new BigDecimal(cachedHighest);
            validateBidAmount(amount, currentCachedBid);
        }
    }

    private void validateBidAmount(BigDecimal incomingAmount, BigDecimal currentHighest) {
        if (incomingAmount.compareTo(currentHighest) <= 0) {
            throw new IllegalArgumentException("Bid amount must be strictly higher than the current highest bid.");
        }
    }

    private AuctionEngine getOrCreateAuctionEngine(UUID auctionId) {
        return engineRepository.findByIdForUpdate(auctionId)
                .orElseGet(() -> engineRepository.save(
                        AuctionEngine.builder()
                                .auctionId(auctionId)
                                .currentHighestBid(BigDecimal.ZERO)
                                .version(0L)
                                .build()
                ));
    }

    private void updateAuctionState(AuctionEngine engine, BigDecimal amount, UUID bidderId) {
        engine.setCurrentHighestBid(amount);
        engine.setHighestBidderId(bidderId);
        engineRepository.save(engine);
    }

    private BidHistory recordBidHistory(UUID auctionId, UUID bidderId, BigDecimal amount) {
        BidHistory historyRecord = BidHistory.builder()
                .auctionId(auctionId)
                .bidderId(bidderId)
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .build();
        return historyRepository.save(historyRecord);
    }

    private void updateCache(String redisKey, BigDecimal amount) {
        try {
            redisTemplate.opsForValue().set(redisKey, amount.toString());
        } catch (Exception e) {
            // Log cache failures as warnings so they don't roll back the DB transaction
            log.warn("Failed to update Redis cache for key {}: {}", redisKey, e.getMessage());
        }
    }
}