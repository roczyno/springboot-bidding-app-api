package com.roczyno.bidding.app.api.request;

import java.time.LocalDate;
import java.util.List;

public record CreateAuctionRequest(
         String title,
         LocalDate startDate,
         LocalDate endDate,
         long timeLeft,
        String distanceCv,
        String location,
         String modelColor,
         String transmission,
         String engineType,
        String startingBid,
        String buyNowPrice,
        long currentBid,
         List<String> images

) {
}
