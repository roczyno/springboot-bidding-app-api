package com.roczyno.bidding.app.api.request;

import java.time.LocalDate;

public record CreateAuctionRequest(
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
