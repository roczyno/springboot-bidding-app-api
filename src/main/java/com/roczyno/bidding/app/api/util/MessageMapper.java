package com.roczyno.bidding.app.api.util;

import com.roczyno.bidding.app.api.model.Message;
import com.roczyno.bidding.app.api.service.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageMapper {
	public MessageResponse toMessageResponse(Message req) {
		return new MessageResponse(
				req.getId(),
				req.getContent(),
				req.getCreatedAt(),
				req.getUser().getId()

		);
	}
}
