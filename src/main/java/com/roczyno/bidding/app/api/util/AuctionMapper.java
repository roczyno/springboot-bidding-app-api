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
                auction.getUser().getId(),
                auction.getUser().getFirstName(),
                auction.getUser().getProfilePic()
        );
    }

	public Auction toAuction(AuctionResponse res) {
		return Auction.builder()
				.id(res.id())
				.title(res.title())
				.startDate(res.startDate())
				.endDate(res.endDate())
				.timeLeft(res.timeLeft())
				.distanceCv(res.distanceCv())
				.location(res.location())
				.modelColor(res.modelColor())
				.transmission(res.transmission())
				.engineType(res.engineType())
				.startingBid(res.startingBid())
				.buyNowPrice(res.buyNowPrice())
				.currentBid(res.currentBid())
				.activeBids(res.activeBids())
				.images(res.images())
				.build();
	}
}
