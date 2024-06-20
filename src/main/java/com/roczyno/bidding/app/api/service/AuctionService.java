package com.roczyno.bidding.app.api.service;


import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.request.CreateAuctionRequest;
import com.roczyno.bidding.app.api.respond.AuctionResponse;

public interface AuctionService {

    AuctionResponse createAuction(CreateAuctionRequest req, User user);
}
