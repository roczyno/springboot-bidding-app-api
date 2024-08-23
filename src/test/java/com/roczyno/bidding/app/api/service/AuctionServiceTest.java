package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.exception.AuctionException;
import com.roczyno.bidding.app.api.model.Auction;
import com.roczyno.bidding.app.api.model.AuctionStatus;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.AuctionRepository;
import com.roczyno.bidding.app.api.request.CreateAuctionRequest;
import com.roczyno.bidding.app.api.response.AuctionResponse;
import com.roczyno.bidding.app.api.util.AuctionMapper;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuctionServiceTest {
	@InjectMocks
	private AuctionServiceImpl auctionService;
	@Mock
	private AuctionRepository auctionRepository;

	@Mock
	private AuctionMapper mapper;

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
	public void saveAuction_successfully() {
		CreateAuctionRequest request = new CreateAuctionRequest(
				"Test Auction",
				LocalDate.now(),
				LocalDate.now().plusDays(10),
				"50,000 km",
				"New York",
				"Red",
				"Automatic",
				"V6",
				1000L,
				5000L,
				0L,
				List.of("image1.jpg", "image2.jpg")
		);

		Auction auction = Auction.builder()
				.title(request.title())
				.location(request.location())
				.createdAt(LocalDateTime.now())
				.startDate(request.startDate())
				.endDate(request.endDate())
				.transmission(request.transmission())
				.buyNowPrice(request.buyNowPrice())
				.distanceCv(request.distanceCv())
				.engineType(request.engineType())
				.startingBid(request.startingBid())
				.timeLeft(ChronoUnit.MILLIS.between(request.startDate().atStartOfDay(), request.endDate().atStartOfDay()))
				.modelColor(request.modelColor())
				.images(request.images())
				.auctionStatus(AuctionStatus.ACTIVE)
				.user(user)
				.build();

		AuctionResponse expectedResponse = new AuctionResponse(
				1,
				"Test Auction",
				LocalDate.now(),
				LocalDate.now().plusDays(10),
				ChronoUnit.MILLIS.between(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(10).atStartOfDay()),
				"50,000 km",
				"New York",
				"Red",
				"Automatic",
				"V6",
				1000L,
				5000L,
				0L,
				0L,
				List.of("image1.jpg", "image2.jpg"),
				AuctionStatus.ACTIVE
		);

		when(auctionRepository.save(any(Auction.class))).thenReturn(auction);
		when(mapper.toAuctionResponse(any(Auction.class))).thenReturn(expectedResponse);

		AuctionResponse response = auctionService.createAuction(request, authentication);

		assertNotNull(response);
		assertEquals(expectedResponse, response);
		verify(auctionRepository).save(any(Auction.class));
		verify(mapper).toAuctionResponse(any(Auction.class));
	}

	@Test
	public void getAuction_successful(){
		Integer auctionId=1;

		AuctionResponse expectedResponse= new AuctionResponse(	1,
				"Test Auction",
				LocalDate.now(),
				LocalDate.now().plusDays(10),
				ChronoUnit.MILLIS.between(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(10).atStartOfDay()),
				"50,000 km",
				"New York",
				"Red",
				"Automatic",
				"V6",
				1000L,
				5000L,
				0L,
				0L,
				List.of("image1.jpg", "image2.jpg"),
				AuctionStatus.ACTIVE);

		Auction auction= new Auction();
		when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
		when(mapper.toAuctionResponse(auction)).thenReturn(expectedResponse);
		AuctionResponse actualResponse= auctionService.getAuction(auctionId);

		assertEquals(expectedResponse,actualResponse);

	}

	@Test
	public void deleteAuction_successful() {
		Integer auctionId = 1;
		Auction auction = new Auction();
		auction.setUser(user);

		when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
		doNothing().when(auctionRepository).delete(any(Auction.class));

		String result = auctionService.deleteAuction(auctionId, authentication);

		assertEquals("Auction deleted successfully", result);
		verify(auctionRepository).delete(any(Auction.class));
	}


	@Test
	public void testDeleteAuction_failure_auctionNoFound(){
		Integer auctionId= 2;
		when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());
		assertThrows(AuctionException.class,()-> auctionService.deleteAuction(auctionId,authentication));
	}
	@Test
	public void deleteAuctionFailure_unAuthorized_access(){
		Integer auctionId=2;
		User differentUser= new User();
		Auction auction= new Auction();
		auction.setUser(differentUser);

		when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
		assertThrows(AuctionException.class,()->auctionService.deleteAuction(auctionId,authentication));

	}

	@Test
	public void close_auctionSuccessful(){
		Integer auctionId=1;
		Auction auction= new Auction();
		auction.setAuctionStatus(AuctionStatus.ACTIVE);
		auction.setUser(user);
		AuctionStatus status= AuctionStatus.CLOSED;
		when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
		when(auctionRepository.save(any(Auction.class))).thenReturn(auction);
		String result= auctionService.closeOrOpenAuction(auctionId,authentication,status);
		assertEquals("Auction closed successfully",result);
		assertEquals(AuctionStatus.CLOSED,auction.getAuctionStatus());
		verify(auctionRepository,times(1)).findById(auctionId);


	}

	@Test
	public void unauthorizedUser_closeAuction_failed(){
		Integer auctionId=1;
		User differentUser= new User();
		Auction auction= new Auction();
		auction.setUser(differentUser);
		auction.setAuctionStatus(AuctionStatus.ACTIVE);
		AuctionStatus status= AuctionStatus.CLOSED;

		when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
		when(auctionRepository.save(any(Auction.class))).thenReturn(auction);
		assertThrows(AuctionException.class,()-> auctionService.closeOrOpenAuction(auctionId,authentication,status));

		verify(auctionRepository,times(1)).findById(auctionId);
		verify(auctionRepository,times(0)).save(auction);

	}

	@Test
	public void auctionNotFound_closeAuction_failed() {
		Integer auctionId=1;
		Auction auction= new Auction();
		auction.setUser(user);
		auction.setAuctionStatus(AuctionStatus.ACTIVE);
		AuctionStatus status= AuctionStatus.CLOSED;
		when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());
		assertThrows(AuctionException.class,()-> auctionService.closeOrOpenAuction(auctionId,authentication,status));
		verify(auctionRepository,times(1)).findById(auctionId);
		verify(auctionRepository,times(0)).save(any(Auction.class));

	}

	@Test
	public void updateAuction_successful() {
		Integer auctionId = 1;
		Auction auction = new Auction();
		auction.setUser(user);

		CreateAuctionRequest updateRequest = new CreateAuctionRequest(
				"Updated Auction",
				LocalDate.now(),
				LocalDate.now().plusDays(10),
				"100,000 km",
				"Los Angeles",
				"Blue",
				"Manual",
				"V8",
				2000L,
				6000L,
				0L,
				List.of("image3.jpg", "image4.jpg")
		);

		Auction updatedAuction = new Auction();
		updatedAuction.setUser(user);
		updatedAuction.setTitle("Updated Auction");
		updatedAuction.setStartDate(LocalDate.now());
		updatedAuction.setEndDate(LocalDate.now().plusDays(10));
		updatedAuction.setDistanceCv("100,000 km");
		updatedAuction.setLocation("Los Angeles");
		updatedAuction.setModelColor("Blue");
		updatedAuction.setTransmission("Manual");
		updatedAuction.setEngineType("V8");
		updatedAuction.setStartingBid(2000L);
		updatedAuction.setBuyNowPrice(6000L);
		updatedAuction.setImages(List.of("image3.jpg", "image4.jpg"));
		updatedAuction.setAuctionStatus(AuctionStatus.ACTIVE);

		AuctionResponse expectedResponse = new AuctionResponse(
				1,
				"Updated Auction",
				LocalDate.now(),
				LocalDate.now().plusDays(10),
				ChronoUnit.MILLIS.between(LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(10).atStartOfDay()),
				"100,000 km",
				"Los Angeles",
				"Blue",
				"Manual",
				"V8",
				2000L,
				6000L,
				0L,
				0L,
				List.of("image3.jpg", "image4.jpg"),
				AuctionStatus.ACTIVE
		);

		when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
		when(auctionRepository.save(any(Auction.class))).thenReturn(updatedAuction);
		when(mapper.toAuctionResponse(updatedAuction)).thenReturn(expectedResponse);

		AuctionResponse actualResponse = auctionService.updateAuction(auctionId, authentication, updateRequest);

		assertEquals(expectedResponse, actualResponse);
		verify(auctionRepository, times(1)).findById(auctionId);
		verify(auctionRepository, times(1)).save(any(Auction.class));
	}

	@Test
	public void updateAuction_unauthorized(){
		Integer auctionId=1;
		User differentUser= new User();
		Auction auction=Auction.builder().user(differentUser).build();
		CreateAuctionRequest updateRequest = new CreateAuctionRequest(
				"Updated Auction",
				LocalDate.now(),
				LocalDate.now().plusDays(10),
				"100,000 km",
				"Los Angeles",
				"Blue",
				"Manual",
				"V8",
				2000L,
				6000L,
				0L,
				List.of("image3.jpg", "image4.jpg")
		);
		when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
		assertThrows(AuctionException.class,()->auctionService.updateAuction(auctionId,authentication,updateRequest));
		verify(auctionRepository, times(1)).findById(auctionId);
		verify(auctionRepository, times(0)).save(any(Auction.class));
	}

	@Test
	public void updateAuction_notFound() {
		Integer auctionId = 1;

		CreateAuctionRequest updateRequest = new CreateAuctionRequest(
				"Updated Auction",
				LocalDate.now(),
				LocalDate.now().plusDays(10),
				"100,000 km",
				"Los Angeles",
				"Blue",
				"Manual",
				"V8",
				2000L,
				6000L,
				0L,
				List.of("image3.jpg", "image4.jpg")
		);

		when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());

		assertThrows(AuctionException.class, () -> auctionService.updateAuction(auctionId, authentication, updateRequest));

		verify(auctionRepository, times(1)).findById(auctionId);
		verify(auctionRepository, times(0)).save(any(Auction.class));
	}

	@Test
	public void getAllAuctions_successful() {
		Pageable pageable = PageRequest.of(0, 10);
		Auction auction1 = new Auction();
		Auction auction2 = new Auction();

		List<Auction> auctionList = List.of(auction1, auction2);
		Page<Auction> auctionPage = new PageImpl<>(auctionList, pageable, auctionList.size());

		AuctionResponse response1 = new AuctionResponse(
				1, "Title1", LocalDate.now(), LocalDate.now().plusDays(10), 0L, "50,000 km",
				"New York", "Red", "Automatic", "V6", 1000L, 5000L, 0L, 0L, List.of("image1.jpg"), AuctionStatus.ACTIVE
		);
		AuctionResponse response2 = new AuctionResponse(
				2, "Title2", LocalDate.now(), LocalDate.now().plusDays(5), 0L, "30,000 km",
				"Los Angeles", "Blue", "Manual", "V8", 2000L, 6000L, 0L, 0L, List.of("image2.jpg"), AuctionStatus.ACTIVE
		);

		when(auctionRepository.findAll(pageable)).thenReturn(auctionPage);
		when(mapper.toAuctionResponse(auction1)).thenReturn(response1);
		when(mapper.toAuctionResponse(auction2)).thenReturn(response2);

		Page<AuctionResponse> responsePage = auctionService.getAllAuctions(pageable);

		assertNotNull(responsePage);
		assertEquals(2, responsePage.getTotalElements());
		assertEquals(2, responsePage.getContent().size());
		assertEquals(response1, responsePage.getContent().get(0));
		assertEquals(response2, responsePage.getContent().get(1));

		verify(auctionRepository, times(1)).findAll(pageable);
		verify(mapper, times(1)).toAuctionResponse(auction1);
		verify(mapper, times(1)).toAuctionResponse(auction2);
	}



}
