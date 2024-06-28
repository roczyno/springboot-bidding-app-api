package com.roczyno.bidding.app.api.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record CreateAuctionRequest(
         String title,
         LocalDate startDate,
         LocalDate endDate,
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
