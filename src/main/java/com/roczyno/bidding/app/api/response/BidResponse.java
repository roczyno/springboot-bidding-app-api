package com.roczyno.bidding.app.api.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record BidResponse(
        Integer id,
        String username,
        LocalDateTime createdAt,
        long amount
) {
}
