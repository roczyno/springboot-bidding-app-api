package com.roczyno.bidding.app.api.controller;

import com.roczyno.bidding.app.api.request.CreateBidRequest;
import com.roczyno.bidding.app.api.service.BidService;
import com.roczyno.bidding.app.api.util.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bid")
public class BidController {
    private final BidService bidService;

    @PostMapping("/auction/{auctionId}")
    public ResponseEntity<Object> addBid(@PathVariable Integer auctionId, @Valid @RequestBody CreateBidRequest bid,
                                         Authentication user) {
        return ResponseHandler.successResponse(bidService.createBid(bid,user,auctionId), HttpStatus.OK);
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<Object> getBidsForAuction(@PathVariable Integer auctionId) {
        return ResponseHandler.successResponse(bidService.getBidForAuction(auctionId),HttpStatus.OK);
    }
}
