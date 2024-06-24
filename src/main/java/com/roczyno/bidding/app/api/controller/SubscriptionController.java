package com.roczyno.bidding.app.api.controller;

import com.roczyno.bidding.app.api.model.PlanType;
import com.roczyno.bidding.app.api.model.Subscription;
import com.roczyno.bidding.app.api.model.User;
import com.roczyno.bidding.app.api.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("subscription")
public class SubscriptionController {
	private final SubscriptionService subscriptionService;


	@PostMapping
	public Subscription createSubscription(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		return subscriptionService.createSubscription(user);
	}

	@GetMapping("/{userId}")
	public Subscription getUserSubscription(@PathVariable Integer userId) {
		return subscriptionService.getUserSubscription(userId);
	}

	@PutMapping("/{userId}/upgrade")
	public Subscription upgradeSubscription(@PathVariable Integer userId, @RequestParam PlanType planType) {
		return subscriptionService.upgradeSubscription(userId, planType);
	}

	@GetMapping("/{userId}/validity")
	public boolean isValidSubscription(@PathVariable Integer userId) {
		Subscription subscription = subscriptionService.getUserSubscription(userId);
		return subscriptionService.isValidSubscription(subscription);
	}
}
