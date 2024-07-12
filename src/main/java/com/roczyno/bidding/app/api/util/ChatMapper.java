package com.roczyno.bidding.app.api.util;

import com.roczyno.bidding.app.api.model.Chat;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.response.ChatResponse;
import com.roczyno.bidding.app.api.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMapper {
	private final UserMapper userMapper;

	public ChatResponse toChatResponse(Chat req) {
		return new ChatResponse(
				req.getId(),
				req.getCreatedAt(),
				mapUsersToUserProfileResponses(req.getUsers())
		);
	}

	private Set<UserProfileResponse> mapUsersToUserProfileResponses(Set<User> users) {
		return users.stream()
				.map(userMapper::toUserResponse)
				.collect(Collectors.toSet());
	}


}
