package com.roczyno.bidding.app.api.request;

import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record CreateBidRequest(
        long amount
) {
}
