package com.roczyno.bidding.app.api.controller;

import com.roczyno.bidding.app.api.request.CreateAuctionRequest;
import com.roczyno.bidding.app.api.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auction")
public class AuctionController {
    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<Object> addAuction(@RequestBody CreateAuctionRequest req, Authentication user){
        return ResponseEntity.ok(auctionService.createAuction(req,user));
    }
}
