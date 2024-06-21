package com.roczyno.bidding.app.api.service;


import com.roczyno.bidding.app.api.request.CreateAuctionRequest;

import com.roczyno.bidding.app.api.response.AuctionResponse;
import org.springframework.security.core.Authentication;

public interface AuctionService {

  AuctionResponse createAuction(CreateAuctionRequest req, Authentication user);
}
