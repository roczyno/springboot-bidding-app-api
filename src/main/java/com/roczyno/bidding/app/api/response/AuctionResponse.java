package com.roczyno.bidding.app.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.roczyno.bidding.app.api.model.AuctionStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;


@Builder
public record AuctionResponse(
        Integer id,
        String title,
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="GMT")
        LocalDate startDate,
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="GMT")
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
		Integer userId,
		String username,
		String userProfilePic

) {
}
