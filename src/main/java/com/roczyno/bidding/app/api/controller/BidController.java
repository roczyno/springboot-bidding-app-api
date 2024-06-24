package com.roczyno.bidding.app.api.controller;

import com.roczyno.bidding.app.api.request.CreateBidRequest;
import com.roczyno.bidding.app.api.service.BidService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Object> addBid(@PathVariable Integer auctionId, @RequestBody CreateBidRequest bid,
                                         Authentication user) {
        return ResponseEntity.ok(bidService.createBid(bid,user,auctionId));
    }

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<Object> getBidsForAuction(@PathVariable Integer auctionId) {
        return ResponseEntity.ok(bidService.getBidForAuction(auctionId));
    }
}
