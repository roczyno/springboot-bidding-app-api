package com.roczyno.bidding.app.api.controller;

import com.roczyno.bidding.app.api.model.AuctionStatus;
import com.roczyno.bidding.app.api.request.CreateAuctionRequest;
import com.roczyno.bidding.app.api.service.AuctionService;
import com.roczyno.bidding.app.api.util.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
        return ResponseHandler.successResponse(auctionService.createAuction(req,user), HttpStatus.OK);
    }
    @GetMapping("/{auctionId}")
    public ResponseEntity<Object> getAuction(@PathVariable Integer auctionId){
        return ResponseHandler.successResponse(auctionService.getAuction(auctionId),HttpStatus.OK);
    }

    @PutMapping("/{auctionId}")
    public ResponseEntity<Object> updateAuction(@PathVariable Integer auctionId, Authentication user,
                                                @RequestBody CreateAuctionRequest req){
        return ResponseEntity.ok(auctionService.updateAuction(auctionId,user,req));
    }
    @DeleteMapping("/{auctionId}")
    public ResponseEntity<Object> deleteAuction(@PathVariable Integer auctionId, Authentication user){
        return ResponseHandler.successResponse(auctionService.deleteAuction(auctionId,user),HttpStatus.OK);
    }
    @PutMapping("/status/{auctionId}")
    public ResponseEntity<Object> changeAuctionStatus(@PathVariable Integer auctionId, @RequestParam AuctionStatus status,
                                                      Authentication user){
        return ResponseHandler.successResponse(auctionService.closeOrOpenAuction(auctionId,user,status),HttpStatus.OK);
    }
    @PutMapping("/closeAutomatic/status/{auctionId}")
    public ResponseEntity<Object> changeAuctionStatus(@PathVariable Integer auctionId, @RequestParam AuctionStatus status){
        return ResponseHandler.successResponse(auctionService.closeAuctionAutomatically(auctionId,status),HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<Object> getAuctions(Pageable pageable, @RequestParam(required = false) String searchTerm,
                                              @RequestParam(required = false) AuctionStatus status){
        return ResponseHandler.successResponse(auctionService.getAllAuctions(pageable,searchTerm,status),HttpStatus.OK);
    }
}
