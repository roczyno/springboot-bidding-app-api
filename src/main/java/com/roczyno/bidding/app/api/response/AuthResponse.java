package com.roczyno.bidding.app.api.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AuthResponse(
        String token
) {
    public static record AuctionResponse(
            String title,
            LocalDate startDate,
            LocalDate endDate,
            int timeLeft,
            String distanceCv,
            String location,
            String modelColor,
            String transmission,
            String engineType,
            String startingBid,
            String buyNowPrice,
            long currentBid
    ) {
    }
}
