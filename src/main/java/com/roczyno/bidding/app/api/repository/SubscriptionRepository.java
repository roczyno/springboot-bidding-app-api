package com.roczyno.bidding.app.api.repository;

import com.roczyno.bidding.app.api.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
}
