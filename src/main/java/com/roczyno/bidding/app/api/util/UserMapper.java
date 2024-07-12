package com.roczyno.bidding.app.api.util;

import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.response.UserProfileResponse;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
	public UserProfileResponse toUserResponse(User user) {
		return new UserProfileResponse(
				user.getId(),
				user.getFirstName(),
				user.getLastName(),
				user.getEmail(),
				user.getProfilePic(),
				user.getPhone()
		);
	}
}
