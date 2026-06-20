package com.auction.catalog.mapper;

import com.auction.catalog.dto.AuctionCreateRequest;
import com.auction.catalog.dto.AuctionResponse;
import com.auction.catalog.model.AuctionItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuctionMapper {

    // Maps inbound request to internal entity model
    @Mapping(target = "id", ignore = true) // Database manages ID allocation
    @Mapping(target = "status", ignore = true) // Service layer governs lifecycle status
    AuctionItem toEntity(AuctionCreateRequest request);

    // Maps internal database entity to outbound public response DTO
    AuctionResponse toResponse(AuctionItem entity);
}