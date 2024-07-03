package com.roczyno.bidding.app.api.util;

import com.roczyno.bidding.app.api.model.Auction;
import com.roczyno.bidding.app.api.response.AuctionResponse;
import org.springframework.stereotype.Service;

@Service
public class AuctionMapper {
    public AuctionResponse toAuctionResponse(Auction auction) {
        return new AuctionResponse(
                auction.getId(),
                auction.getTitle(),
                auction.getStartDate(),
                auction.getEndDate(),
                auction.getTimeLeft(),
                auction.getDistanceCv(),
                auction.getLocation(),
                auction.getModelColor(),
                auction.getTransmission(),
                auction.getEngineType(),
                auction.getStartingBid(),
                auction.getBuyNowPrice(),
                auction.getCurrentBid(),
                auction.getActiveBids(),
                auction.getImages(),
                auction.getAuctionStatus(),
                auction.getUser().getFirstName(),
                auction.getUser().getProfilePic()
        );
    }
}
