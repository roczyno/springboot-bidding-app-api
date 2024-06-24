package com.roczyno.bidding.app.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Builder
public class Subscription {
	@Id
	@GeneratedValue
	private Integer id;
	private LocalDate subscriptionStartDate;
	private LocalDate subscriptionEndDate;
	private boolean isSubscriptionValid;
	private PlanType planType;
	@OneToOne
	private User user;
}
