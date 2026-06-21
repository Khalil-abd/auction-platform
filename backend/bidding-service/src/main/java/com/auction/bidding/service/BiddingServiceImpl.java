package com.auction.bidding.service;

import com.auction.bidding.event.BidPlacedEvent;
import com.auction.bidding.exception.InvalidBidException;
import com.auction.bidding.model.AuctionEngine;
import com.auction.bidding.model.BidHistory;
import com.auction.bidding.repository.AuctionEngineRepository;
import com.auction.bidding.repository.BidHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BiddingServiceImpl implements BiddingService {

    private final AuctionEngineRepository engineRepository;
    private final BidHistoryRepository historyRepository;
    private final StringRedisTemplate redisTemplate;

    // Inject the Kafka template managed by Spring Boot
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.redis.bids-prefix}")
    private String redisPrefix;

    @Value("${app.kafka.bids-topic}")
    private String kafkaBidsTopic;

    @Override
    @Transactional
    public BidHistory placeBid(UUID auctionId, UUID bidderId, BigDecimal amount) {
        String redisKey = redisPrefix + auctionId;

        validateAgainstCache(redisKey, amount);

        AuctionEngine engine = getOrCreateAuctionEngine(auctionId);
        validateBidAmount(amount, engine.getCurrentHighestBid());

        updateAuctionState(engine, amount, bidderId);
        BidHistory savedHistory = recordBidHistory(auctionId, bidderId, amount);

        updateCache(redisKey, amount);

        // Emit Kafka event asynchronously
        emitBidPlacedEvent(savedHistory);

        log.info("Successfully accepted bid of {} on auction {} by user {}", amount, auctionId, bidderId);
        return savedHistory;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BidHistory> getBidHistory(UUID auctionId, Pageable pageable) {
        return historyRepository.findByAuctionId(auctionId, pageable);
    }

    // --- Private Helper Methods ---

    private void emitBidPlacedEvent(BidHistory history) {
        BidPlacedEvent event = BidPlacedEvent.builder()
                .auctionId(history.getAuctionId())
                .bidderId(history.getBidderId())
                .amount(history.getAmount())
                .timestamp(history.getTimestamp())
                .build();

        // Send to topic using auctionId as the message key to maintain order guarantee per auction
        kafkaTemplate.send(kafkaBidsTopic, event.getAuctionId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish BidPlacedEvent to Kafka for auction {}: {}", event.getAuctionId(), ex.getMessage());
                    } else {
                        log.debug("Successfully published BidPlacedEvent to partition {}", result.getRecordMetadata().partition());
                    }
                });
    }

    private void validateAgainstCache(String redisKey, BigDecimal amount) {
        String cachedHighest = redisTemplate.opsForValue().get(redisKey);
        if (cachedHighest != null) {
            BigDecimal currentCachedBid = new BigDecimal(cachedHighest);
            validateBidAmount(amount, currentCachedBid);
        }
    }

    private void validateBidAmount(BigDecimal incomingAmount, BigDecimal currentHighest) {
        if (incomingAmount.compareTo(currentHighest) <= 0) {
            throw new InvalidBidException("Bid amount must be strictly higher than the current highest bid.");
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
                .timestamp(java.time.LocalDateTime.now())
                .build();
        return historyRepository.save(historyRecord);
    }

    private void updateCache(String redisKey, BigDecimal amount) {
        try {
            redisTemplate.opsForValue().set(redisKey, amount.toString());
        } catch (Exception e) {
            log.warn("Failed to update Redis cache for key {}: {}", redisKey, e.getMessage());
        }
    }
}