package com.roczyno.bidding.app.api.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreateAuctionRequest(
         String title,
         LocalDate startDate,
         LocalDate endDate,
         long  timeLeft,
        String distanceCv,
        String location,
         String modelColor,
         String transmission,
         String engineType,
         long startingBid,
         long  buyNowPrice,
         long  currentBid,
         List<String> images

) {
}
