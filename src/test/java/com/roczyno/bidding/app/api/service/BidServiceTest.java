package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.exception.AuctionException;
import com.roczyno.bidding.app.api.model.Auction;
import com.roczyno.bidding.app.api.model.AuctionStatus;
import com.roczyno.bidding.app.api.model.Bid;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.AuctionRepository;
import com.roczyno.bidding.app.api.repository.BidRepository;
import com.roczyno.bidding.app.api.request.CreateBidRequest;
import com.roczyno.bidding.app.api.response.BidResponse;
import com.roczyno.bidding.app.api.util.BidMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BidServiceTest {
	@InjectMocks
	private BidServiceImpl bidService;

	@Mock
	private BidRepository bidRepository;

	@Mock
	private BidMapper mapper;

	@Mock
	private AuctionRepository auctionRepository;

	@Mock
	private Authentication authentication;

	private User user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		user = new User();
		when(authentication.getPrincipal()).thenReturn(user);
	}

	@Test
	public void testCreateBid_successful_whenBidLessThanBuyNowPriceAndCurrentBid() {
		Integer auctionId = 1;
		Auction auction = Auction.builder()
				.buyNowPrice(2000L)
				.currentBid(10L)
				.build();
		CreateBidRequest request = CreateBidRequest.builder()
				.amount(100L)
				.build();
		Bid savedBid = Bid.builder()
				.user(user)
				.auction(auction)
				.amount(100L)
				.createdAt(LocalDateTime.now())
				.build();
		Auction updatedAuction = Auction.builder()
				.buyNowPrice(2000L)
				.currentBid(100L)
				.activeBids(1)
				.auctionStatus(AuctionStatus.ACTIVE)
				.build();

		when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
		when(bidRepository.save(any(Bid.class))).thenReturn(savedBid);
		when(auctionRepository.save(any(Auction.class))).thenReturn(updatedAuction);

		String response = bidService.createBid(request, authentication, auctionId);

		assertEquals("Bid added successfully", response);
		assertEquals(request.amount(), updatedAuction.getCurrentBid());
		verify(auctionRepository, times(1)).findById(auctionId);
		verify(bidRepository, times(1)).save(any(Bid.class));
		verify(auctionRepository, times(1)).save(any(Auction.class));
	}

	@Test
	public void testCreateBid_successful_whenBidGreaterThanBuyNowPriceAndCurrentBid() {
		Integer auctionId = 1;
		Auction auction = Auction.builder()
				.buyNowPrice(2000L)
				.currentBid(10L)
				.auctionStatus(AuctionStatus.ACTIVE)
				.build();
		CreateBidRequest request = CreateBidRequest.builder()
				.amount(100000L)
				.build();
		Bid savedBid = Bid.builder()
				.user(user)
				.auction(auction)
				.amount(100000L)
				.createdAt(LocalDateTime.now())
				.build();
		Auction updatedAuction = Auction.builder()
				.buyNowPrice(2000L)
				.currentBid(100000L)
				.auctionStatus(AuctionStatus.CLOSED)
				.build();

		when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
		when(bidRepository.save(any(Bid.class))).thenReturn(savedBid);
		when(auctionRepository.save(any(Auction.class))).thenReturn(updatedAuction);

		String response = bidService.createBid(request, authentication, auctionId);

		assertEquals("Congratulations. You have successfully won the bid", response);
		assertEquals(request.amount(), updatedAuction.getCurrentBid());
		assertEquals(AuctionStatus.CLOSED, updatedAuction.getAuctionStatus());
		verify(auctionRepository, times(1)).findById(auctionId);
		verify(bidRepository, times(1)).save(any(Bid.class));
		verify(auctionRepository, times(2)).save(any(Auction.class)); // Once for updating the bid and once for closing the auction
	}

	@Test
	public void getBidsForAuction_success() {
		Integer auctionId = 1;
		Bid bid1 = Bid.builder().build();
		Bid bid2 = Bid.builder().build();
		List<Bid> bids = new ArrayList<>();
		bids.add(bid1);
		bids.add(bid2);

		List<BidResponse> expectedBidResponses = List.of(
				new BidResponse(1, "jacob", LocalDateTime.now(), 200L),
				new BidResponse(2, "jacob", LocalDateTime.now(), 300L)
		);

		when(bidRepository.findByAuctionId(auctionId)).thenReturn(bids);
		when(mapper.toBidResponse(bid1)).thenReturn(expectedBidResponses.get(0));
		when(mapper.toBidResponse(bid2)).thenReturn(expectedBidResponses.get(1));

		List<BidResponse> actualResponse = bidService.getBidForAuction(auctionId);

		assertEquals(expectedBidResponses, actualResponse);
		verify(bidRepository, times(1)).findByAuctionId(auctionId);
		verify(mapper, times(1)).toBidResponse(bid1);
		verify(mapper, times(1)).toBidResponse(bid2);
	}
}
