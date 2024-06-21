package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.model.Auction;
import com.roczyno.bidding.app.api.repository.AuctionRepository;
import com.roczyno.bidding.app.api.request.CreateAuctionRequest;

import com.roczyno.bidding.app.api.response.AuctionResponse;
import com.roczyno.bidding.app.api.util.AuctionMapper;
import lombok.RequiredArgsConstructor;
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
    public AuctionResponse createAuction(CreateAuctionRequest req, Authentication user) {
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
                .build();

        Auction savedAuction = auctionRepository.save(auction);
        return mapper.toAuctionResponse(savedAuction);
    }
}
