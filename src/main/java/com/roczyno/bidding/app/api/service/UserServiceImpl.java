package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.repository.UserRepository;
import com.roczyno.bidding.app.api.request.UpdateUserRequest;
import com.roczyno.bidding.app.api.response.UserProfileResponse;
import com.roczyno.bidding.app.api.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing users, including retrieving, updating profiles, and tracking auctions and bids.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	private final UserRepository userRepository;
	private final UserMapper mapper;

	/**
	 * Retrieves all users from the repository and converts them to UserProfileResponse objects.
	 *
	 * @return a list of UserProfileResponse for all users.
	 */
	@Override
	public List<UserProfileResponse> getAllUsers() {
		return userRepository.findAll()
				.stream()
				.map(mapper::toUserResponse)
				.collect(Collectors.toList());
	}

	/**
	 * Retrieves a user by their ID.
	 *
	 * @param id the ID of the user.
	 * @return a UserProfileResponse object containing the user's profile information.
	 * @throws RuntimeException if the user is not found.
	 */
	@Override
	public UserProfileResponse getUserById(Integer id) {
		var user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("User with id " + id + " not found"));
		return mapper.toUserResponse(user);
	}

	/**
	 * Updates a user's profile information such as first name, last name, phone, and profile picture.
	 *
	 * @param userId the ID of the user to update.
	 * @param req    the update request containing the new profile information.
	 * @return a UserProfileResponse object with the updated user profile.
	 * @throws RuntimeException if the user is not found.
	 */
	@Override
	public UserProfileResponse updateUserProfile(Integer userId, UpdateUserRequest req) {
		var user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User with id " + userId +
				" not found"));
		if(req.firstName()!=null){
			user.setFirstName(req.firstName());
		}
		if(req.lastName()!=null){
			user.setLastName(req.lastName());
		}
		if(req.phone()!=null){
			user.setPhone(req.phone());
		}
		if(req.profilePic()!=null){
			user.setProfilePic(req.profilePic());
		}
		var savedUser = userRepository.save(user);
		return mapper.toUserResponse(savedUser);
	}

	/**
	 * Increments the number of auctions created by the user and saves the updated user data.
	 *
	 * @param user the user whose auction count is to be incremented.
	 */
	@Override
	public void setNumberOfAuctionsCreated(User user) {
		user.setNumberOfAuctionsCreated(user.getNumberOfAuctionsCreated()+1);
		userRepository.save(user);
	}

	/**
	 * Increments the number of bids created by the user and saves the updated user data.
	 *
	 * @param user the user whose bid count is to be incremented.
	 */
	@Override
	public void setNumberOfBidsCreated(User user) {
		user.setNumberOfBids(user.getNumberOfBids()+1);
		userRepository.save(user);
	}
}
