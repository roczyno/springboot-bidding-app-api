package com.roczyno.bidding.app.api.service;


import com.roczyno.bidding.app.api.model.AuctionStatus;
import com.roczyno.bidding.app.api.request.CreateAuctionRequest;

import com.roczyno.bidding.app.api.response.AuctionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface AuctionService {
  AuctionResponse createAuction(CreateAuctionRequest req, Authentication user);
  Page<AuctionResponse> getAllAuctions(Pageable pageable);
  AuctionResponse getAuction(Integer auctionId);
  String deleteAuction(Integer auctionId, Authentication user);
  AuctionResponse updateAuction(Integer auctionId,Authentication user,CreateAuctionRequest req);
  String closeOrOpenAuction(Integer auctionId, Authentication user, AuctionStatus status);

  String closeAuctionAutomatically(Integer auctionId, AuctionStatus status);
}
