package com.roczyno.bidding.app.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BidResponse(
        Integer id,
        String username,
        LocalDateTime createdAt,
        long amount
) {
}
