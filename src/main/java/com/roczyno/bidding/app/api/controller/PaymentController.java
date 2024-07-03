package com.roczyno.bidding.app.api.controller;

import com.roczyno.bidding.app.api.request.PaymentRequest;
import com.roczyno.bidding.app.api.request.VerifyTransactionRequest;
import com.roczyno.bidding.app.api.response.PaymentResponse;
import com.roczyno.bidding.app.api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping("/initialize")
	public ResponseEntity<PaymentResponse> initializeTransaction(@RequestBody PaymentRequest request) {
		try {
			PaymentResponse response = paymentService.initializeTransaction(request);
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}

	@PostMapping("/verify")
	public ResponseEntity<String> verifyTransaction(@RequestBody VerifyTransactionRequest request) {
		try {
			String response = paymentService.verifyTransaction(request.reference());
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
