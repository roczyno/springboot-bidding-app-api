package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.exception.AuctionException;
import com.roczyno.bidding.app.api.model.Auction;
import com.roczyno.bidding.app.api.model.AuctionStatus;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.AuctionRepository;
import com.roczyno.bidding.app.api.request.CreateAuctionRequest;

import com.roczyno.bidding.app.api.response.AuctionResponse;
import com.roczyno.bidding.app.api.util.AuctionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;
    private final AuctionMapper mapper;
    @Override
    public AuctionResponse createAuction(CreateAuctionRequest req, Authentication connectedUser) {
        User user =( (User) connectedUser.getPrincipal());
        LocalDate startDate = req.startDate();
        LocalDate endDate = req.endDate();

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date should be after the start date.");
        }

        long remainingTime = ChronoUnit.MILLIS.between(startDate.atStartOfDay(), endDate.atStartOfDay());

        Auction auction = Auction.builder()
                .title(req.title())
                .location(req.location())
                .createdAt(LocalDateTime.now())
                .startDate(startDate)
                .endDate(endDate)
                .transmission(req.transmission())
                .buyNowPrice(req.buyNowPrice())
                .distanceCv(req.distanceCv())
                .engineType(req.engineType())
                .startingBid(req.startingBid())
                .timeLeft(remainingTime)
                .modelColor(req.modelColor())
                .images(req.images())
                .auctionStatus(AuctionStatus.ACTIVE)
                .user(user)
                .build();

        Auction savedAuction = auctionRepository.save(auction);
        return mapper.toAuctionResponse(savedAuction);
    }

    @Override
    public Page<AuctionResponse> getAllAuctions(Pageable pageable) {
        Page<Auction> auctions = auctionRepository.findAll(pageable);
        return auctions.map(mapper::toAuctionResponse);
    }

    @Override
    public AuctionResponse getAuction(Integer auctionId) {
        var auction= auctionRepository.findById(auctionId)
                .orElseThrow(()-> new AuctionException("Auction not found"));
        return mapper.toAuctionResponse(auction);
    }

    @Override
    public String deleteAuction(Integer auctionId, Authentication connectedUser) {
        User user=((User) connectedUser.getPrincipal());
        Auction auction= auctionRepository.findById(auctionId)
                .orElseThrow(()-> new AuctionException("Auction not found"));
        if(!auction.getUser().equals(user)){
            throw new AuctionException("You are not allowed to delete this auction");
        }
        auctionRepository.delete(auction);
        return "Auction deleted successfully";
    }



    @Override
    public AuctionResponse updateAuction(Integer auctionId, Authentication connectedUser, CreateAuctionRequest req) {
        User user = (User) connectedUser.getPrincipal();

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionException("Auction not found"));

        if (!auction.getUser().equals(user)) {
            throw new AuctionException("You are not allowed to update this auction");
        }

        boolean isStartDateUpdated = req.startDate() != null;
        boolean isEndDateUpdated = req.endDate() != null;

        if (isStartDateUpdated) {
            auction.setStartDate(req.startDate());
        }
        if (isEndDateUpdated) {
            auction.setEndDate(req.endDate());
        }

        if (isStartDateUpdated || isEndDateUpdated) {
            LocalDate startDate = isStartDateUpdated ? req.startDate() : auction.getStartDate();
            LocalDate endDate = isEndDateUpdated ? req.endDate() : auction.getEndDate();

            if (endDate.isBefore(startDate)) {
                throw new AuctionException("End date should be after the start date");
            }

            long timeLeft = ChronoUnit.MILLIS.between(LocalDateTime.now(), endDate.atStartOfDay());
            auction.setTimeLeft(timeLeft);
        }

        if (req.title() != null) {
            auction.setTitle(req.title());
        }
        if (req.location() != null) {
            auction.setLocation(req.location());
        }
        if (req.transmission() != null) {
            auction.setTransmission(req.transmission());
        }

            auction.setBuyNowPrice(req.buyNowPrice());

        if (req.distanceCv() != null) {
            auction.setDistanceCv(req.distanceCv());
        }
        if (req.engineType() != null) {
            auction.setEngineType(req.engineType());
        }

            auction.setStartingBid(req.startingBid());


        Auction updatedAuction = auctionRepository.save(auction);
        return mapper.toAuctionResponse(updatedAuction);
    }



    @Override
    public String closeOrOpenAuction(Integer auctionId, Authentication connectedUser, AuctionStatus status) {
        User user = (User) connectedUser.getPrincipal();

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionException("Auction not found"));

        if (!auction.getUser().equals(user)) {
            throw new AuctionException("You are not allowed to close this auction");
        }
        auction.setAuctionStatus(status);
        auctionRepository.save(auction);
        return "Auction closed successfully";
    }
}
