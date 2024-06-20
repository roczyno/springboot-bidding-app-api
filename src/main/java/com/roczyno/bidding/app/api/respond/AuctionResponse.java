package com.roczyno.bidding.app.api.respond;

import java.time.LocalDate;

public record AuctionResponse(
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
