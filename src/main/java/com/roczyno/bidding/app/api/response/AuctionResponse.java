package com.roczyno.bidding.app.api.response;

import com.roczyno.bidding.app.api.model.AuctionStatus;

import java.time.LocalDate;
import java.util.List;

public record AuctionResponse(
        Integer id,
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
        List<String> images,
        AuctionStatus status
) {
}
