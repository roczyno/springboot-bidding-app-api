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

/**
 * Service implementation for managing bids on auctions.
 */
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


    /**
     * Places a bid on an auction for the authenticated user.
     * The user is allowed to bid only once per auction. The bid amount must be higher than the current bid.
     * The bid can also immediately win the auction if it is greater than or equal to the "buy now" price.
     *
     * @param req the request containing bid details (amount).
     * @param connectedUser the authenticated user placing the bid.
     * @param auctionId the ID of the auction.
     * @return a success message indicating the result of the bid placement.
     * @throws AuctionException if the user exceeds the allowed number of bids or tries to place a duplicate bid.
     * @throws BidException if the user exceeds the bid limits for their subscription plan.
     */
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

    /**
     * Validates if the user has already placed a bid on the auction.
     *
     * @param user the authenticated user.
     * @param auctionId the ID of the auction.
     * @throws AuctionException if the user has already placed a bid.
     */
    private void validateUserBid(User user, Integer auctionId) {
        boolean userHasBid = bidRepository.existsByUserAndAuctionId(user, auctionId);
        if (userHasBid) {
            throw new AuctionException("You can only bid once per auction");
        }
    }

    /**
     * Validates if the bid amount is higher than the current bid on the auction.
     *
     * @param amount the amount of the bid.
     * @param auction the auction being bid on.
     * @throws AuctionException if the bid amount is lower than or equal to the current bid.
     */
    private void validateBidAmount(long amount, Auction auction) {
        if (amount <= auction.getCurrentBid()) {
            throw new AuctionException("Bid amount must be higher than the current bid");
        }
    }

    /**
     * Updates the auction details after a new bid is placed.
     * This includes updating the current bid amount and the number of active bids.
     *
     * @param auction the auction to update.
     * @param bid the new bid that was placed.
     */
    private void updateAuctionAfterBid(Auction auction, Bid bid) {
        auctionService.setCurrentBidForAuction(bid.getAmount(),auction);
        auctionService.setActiveBidsForAuction(auction);
    }

    /**
     * Retrieves a list of all bids placed on a specific auction.
     *
     * @param auctionId the ID of the auction.
     * @return a list of bids for the auction.
     * @throws AuctionException if no bids are found for the auction.
     */
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
