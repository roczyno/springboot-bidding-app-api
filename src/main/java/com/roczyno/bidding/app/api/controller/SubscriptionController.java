package com.roczyno.bidding.app.api.controller;

import com.roczyno.bidding.app.api.model.PlanType;
import com.roczyno.bidding.app.api.service.SubscriptionService;
import com.roczyno.bidding.app.api.util.ResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("subscription")
public class SubscriptionController {
	private final SubscriptionService subscriptionService;


	@GetMapping("/{userId}")
	public ResponseEntity<Object> getUserSubscription(@PathVariable Integer userId) {
		return ResponseHandler.successResponse(subscriptionService.getUserSubscription(userId),HttpStatus.OK);
	}

	@PutMapping("/{userId}/upgrade")
	public ResponseEntity<Object> upgradeSubscription(@PathVariable Integer userId, @RequestParam PlanType planType) {
		return ResponseHandler.successResponse( subscriptionService.upgradeSubscription(userId, planType),HttpStatus.OK);
	}

}
