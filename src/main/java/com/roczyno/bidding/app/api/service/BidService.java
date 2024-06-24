package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.request.CreateBidRequest;
import com.roczyno.bidding.app.api.response.BidResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface BidService {
    String createBid(CreateBidRequest req, Authentication connectedUser, Integer auctionId);
    List<BidResponse> getBidForAuction(Integer auctionId);
}
