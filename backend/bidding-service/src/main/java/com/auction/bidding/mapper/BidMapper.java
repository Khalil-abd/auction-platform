package com.auction.bidding.mapper;

import com.auction.bidding.dto.BidResponse;
import com.auction.bidding.model.BidHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BidMapper {
    BidResponse toResponse(BidHistory entity);
}