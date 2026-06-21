package com.auction.catalog.controller;

import com.auction.catalog.dto.AuctionCreateRequest;
import com.auction.catalog.dto.AuctionResponse;
import com.auction.catalog.mapper.AuctionMapper;
import com.auction.catalog.model.AuctionItem;
import com.auction.catalog.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class CatalogController {

    private final AuctionService auctionService;
    private final AuctionMapper auctionMapper;

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(
            @RequestHeader("X-User-Id") String authenticatedUserId,
            @RequestBody AuctionCreateRequest request) {
        AuctionItem itemEntity = auctionMapper.toEntity(request);
        itemEntity.setSellerId(authenticatedUserId);

        AuctionItem savedItem = auctionService.createAuction(itemEntity);

        return ResponseEntity.status(HttpStatus.CREATED).body(auctionMapper.toResponse(savedItem));
    }

    @GetMapping
    public ResponseEntity<Page<AuctionResponse>> getAllActiveAuctions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Fetch paginated entities and map structural items inline
        Page<AuctionResponse> responsePage = auctionService.getActiveAuctions(pageable)
                .map(auctionMapper::toResponse);

        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponse> getAuctionById(@PathVariable String id) {
        return auctionService.getAuctionById(id)
                .map(auctionMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}