package com.roczyno.bidding.app.api.response;

import com.roczyno.bidding.app.api.model.AuctionStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;


@Builder
public record AuctionResponse(
        Integer id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        Long timeLeft,
        String distanceCv,
        String location,
        String modelColor,
        String transmission,
        String engineType,
        long  startingBid,
        long  buyNowPrice,
        long currentBid,
        long activeBids,
        List<String> images,
        AuctionStatus status,
		String username,
		String userProfilePic

) {
}
