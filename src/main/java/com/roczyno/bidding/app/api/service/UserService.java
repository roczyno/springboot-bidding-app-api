package com.roczyno.bidding.app.api.service;

import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.request.UpdateUserRequest;
import com.roczyno.bidding.app.api.response.UserProfileResponse;

import java.util.List;

public interface UserService {
	List<UserProfileResponse> getAllUsers();
	UserProfileResponse getUserById(Integer id);
	UserProfileResponse updateUserProfile(Integer userId, UpdateUserRequest req);
	void setNumberOfAuctionsCreated(User user);
	void setNumberOfBidsCreated(User user);
}
