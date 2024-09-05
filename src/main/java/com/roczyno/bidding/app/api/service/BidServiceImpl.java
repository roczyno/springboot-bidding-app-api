package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.exception.AuctionException;
import com.roczyno.bidding.app.api.exception.BidException;
import com.roczyno.bidding.app.api.model.Auction;
import com.roczyno.bidding.app.api.model.Bid;
import com.roczyno.bidding.app.api.model.PlanType;
import com.roczyno.bidding.app.api.model.Subscription;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.BidRepository;
import com.roczyno.bidding.app.api.request.CreateBidRequest;
import com.roczyno.bidding.app.api.response.BidResponse;
import com.roczyno.bidding.app.api.util.AuctionMapper;
import com.roczyno.bidding.app.api.util.BidMapper;
import com.roczyno.bidding.app.api.util.SubscriptionResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
    private final BidRepository bidRepository;
    private final AuctionService auctionService;
    private final AuctionMapper auctionMapper;
    private final BidMapper bidMapper;
    private final SubscriptionService subscriptionService;
    private final SubscriptionResponseMapper subscriptionResponseMapper;
    private final UserService userService;


    @Transactional
    @Override
    public String createBid(CreateBidRequest req, Authentication connectedUser, Integer auctionId) {
        Auction auction =auctionMapper.toAuction(auctionService.getAuction(auctionId));
        User user = (User) connectedUser.getPrincipal();
        Subscription subscription = subscriptionResponseMapper
                .toSubscription(subscriptionService.getUserSubscription(user.getId()));

        if (subscription.getPlanType() == PlanType.BASIC && bidRepository.countByUserId(user.getId()) >= 2) {
            throw new AuctionException("Users on a BASIC plan can only place one bid");
        }
        else if(subscription.getPlanType()==PlanType.STANDARD && bidRepository.countByUserId(user.getId()) >= 5) {
            throw new BidException("Users on a STANDARD plan can only place five bid");
        }
        validateUserBid(user, auctionId);
        validateBidAmount(req.amount(), auction);

        Bid bid = Bid.builder()
                .amount(req.amount())
                .auction(auction)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
        bidRepository.save(bid);
        updateAuctionAfterBid(auction, bid);

        if (req.amount() >= auction.getBuyNowPrice()) {
            auctionService.closeAuctionForBuyNow(auction);
            return "Congratulations. You have successfully won the bid";
        }

        userService.setNumberOfBidsCreated(user);
        return "Bid added successfully";
    }

    private void validateUserBid(User user, Integer auctionId) {
        boolean userHasBid = bidRepository.existsByUserAndAuctionId(user, auctionId);
        if (userHasBid) {
            throw new AuctionException("You can only bid once per auction");
        }
    }

    private void validateBidAmount(long amount, Auction auction) {
        if (amount <= auction.getCurrentBid()) {
            throw new AuctionException("Bid amount must be higher than the current bid");
        }
    }

    private void updateAuctionAfterBid(Auction auction, Bid bid) {
        auctionService.setCurrentBidForAuction(bid.getAmount(),auction);
        auctionService.setActiveBidsForAuction(auction);
    }

    @Override
    public List<BidResponse> getBidForAuction(Integer auctionId) {
        List<Bid> bids = bidRepository.findByAuctionId(auctionId);
        if (bids.isEmpty()) {
            throw new AuctionException("No bids found for the specified auction");
        }
        return bids.stream()
                .map(bidMapper::toBidResponse)
                .collect(Collectors.toList());
    }


}
