package com.roczyno.bidding.app.api.controller;

import com.roczyno.bidding.app.api.model.AuctionStatus;
import com.roczyno.bidding.app.api.request.CreateAuctionRequest;
import com.roczyno.bidding.app.api.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @GetMapping("/{auctionId}")
    public ResponseEntity<Object> getAuction(@PathVariable Integer auctionId){
        return ResponseEntity.ok(auctionService.getAuction(auctionId));
    }

    @PutMapping("/{auctionId}")
    public ResponseEntity<Object> updateAuction(@PathVariable Integer auctionId, Authentication user,
                                                @RequestBody CreateAuctionRequest req){
        return ResponseEntity.ok(auctionService.updateAuction(auctionId,user,req));
    }
    @DeleteMapping("/{auctionId}")
    public ResponseEntity<Object> deleteAuction(@PathVariable Integer auctionId, Authentication user){
        return ResponseEntity.ok(auctionService.deleteAuction(auctionId,user));
    }
    @PutMapping("/status/{auctionId}")
    public ResponseEntity<Object> changeAuctionStatus(@PathVariable Integer auctionId, @RequestParam AuctionStatus status,
                                                      Authentication user){
        return ResponseEntity.ok(auctionService.closeOrOpenAuction(auctionId,user,status));
    }
    @PutMapping("/closeAutomatic/status/{auctionId}")
    public ResponseEntity<Object> changeAuctionStatus(@PathVariable Integer auctionId, @RequestParam AuctionStatus status){
        return ResponseEntity.ok(auctionService.closeAuctionAutomatically(auctionId,status));
    }
    @GetMapping
    public ResponseEntity<Object> getAuctions(Pageable pageable){
        return ResponseEntity.ok(auctionService.getAllAuctions(pageable));
    }
}
