package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.UserRepository;
import com.roczyno.bidding.app.api.request.UpdateUserRequest;
import com.roczyno.bidding.app.api.response.UserProfileResponse;
import com.roczyno.bidding.app.api.util.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {
	@InjectMocks
	private UserServiceImpl userService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private UserMapper mapper;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void test_getUser_success(){
		Integer userId= 1;
		UserProfileResponse expectedResponse = new UserProfileResponse("jacob",
				"adiaba","jacob@gmail.com","gsgsd","0548562559");

		User user= new User();
		user.setId(1);
		user.setFirstName("jacob");
		user.setLastName("adiaba");
		user.setEmail("jacob@gmail.com");

		when(userRepository.findById(userId)).thenReturn(Optional.of((user)));
		when(mapper.toUserResponse(user)).thenReturn(expectedResponse);

		UserProfileResponse userProfileResponse= userService.getUserById(userId);
		assertEquals(expectedResponse,userProfileResponse);
		verify(userRepository,times(1)).findById(userId);
		verify(mapper,times(1)).toUserResponse(user);

	}

	@Test
	public void test_getUser_notSuccessful(){
		Integer userId=1;
		when(userRepository.findById(userId)).thenReturn(null);
		assertThrows(RuntimeException.class,()->userService.getUserById(userId));
		verify(userRepository,times(1)).findById(userId);
	}

	@Test
	public void updateUser(){
		Integer userId=1;
		User user= new User();
		UpdateUserRequest updateUserRequest= new UpdateUserRequest("firstname",
				"lastName","profilePic","4808035");

		User updatedUser = new User();
		updatedUser.setFirstName("firstname");
		updatedUser.setLastName("lastName");
		updatedUser.setProfilePic("profilePic");
		updatedUser.setPhone("4808035");

		UserProfileResponse expectedResponse= new UserProfileResponse("firstname","lastName",
				"email", "profilePic","sjkgjs");
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userRepository.save(any(User.class))).thenReturn(updatedUser);
		when(mapper.toUserResponse(updatedUser)).thenReturn(expectedResponse);
		UserProfileResponse actualResponse= userService.updateUserProfile(userId,updateUserRequest);
		assertEquals(expectedResponse,actualResponse);
	}



	@Test
	public void getAllUser_successful(){
		List<User> users= new ArrayList<>();
		User user1= User.builder()
				.id(1)
				.firstName("adiaba")
				.lastName("jacob")
				.email("jacob@gmail.com")
				.build();

		User user2=User.builder()
				.id(2)
				.firstName("roczyno")
				.lastName("adiaba")
				.email("roczyno@gmail.com")
				.build();

		users.add(user1);
		users.add(user2);

		List<UserProfileResponse> expectedResponse= users
				.stream()
				.map(user -> new UserProfileResponse(
						user.getFirstName(),
						user.getLastName(),
						user.getEmail(),
						user.getProfilePic(),
						user.getPhone()
		)).toList();

		when(userRepository.findAll()).thenReturn(users);
		when(mapper.toUserResponse(user1)).thenReturn(expectedResponse.get(0));
		when(mapper.toUserResponse(user2)).thenReturn(expectedResponse.get(1));

		List<UserProfileResponse> actualResponse= userService.getAllUsers();
		assertEquals(expectedResponse,actualResponse);
		verify(userRepository,times(1)).findAll();

	}
}
