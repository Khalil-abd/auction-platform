package com.auction.bidding.controller;

import com.auction.bidding.dto.BidResponse;
import com.auction.bidding.dto.PlaceBidRequest;
import com.auction.bidding.mapper.BidMapper;
import com.auction.bidding.model.BidHistory;
import com.auction.bidding.service.BiddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bids")
@RequiredArgsConstructor
public class BiddingController {

    private final BiddingService biddingService;
    private final BidMapper bidMapper;

    @PostMapping
    public ResponseEntity<BidResponse> placeBid(@RequestBody PlaceBidRequest request) {
        BidHistory placedBid = biddingService.placeBid(
                request.getAuctionId(),
                request.getBidderId(),
                request.getAmount()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(bidMapper.toResponse(placedBid));
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<Page<BidResponse>> getBidsForAuction(
            @PathVariable UUID auctionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<BidResponse> historyPage = biddingService.getBidHistory(auctionId, pageable)
                .map(bidMapper::toResponse);

        return ResponseEntity.ok(historyPage);
    }
}