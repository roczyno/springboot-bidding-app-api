package com.roczyno.bidding.app.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roczyno.bidding.app.api.request.PaymentRequest;
import com.roczyno.bidding.app.api.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentService {

	@Value("${spring.application.payStack.secretKey}")
	private String payStackSecret;

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	private static final String PAYSTACK_INIT_URL = "https://api.paystack.co/transaction/initialize";
	private static final String PAYSTACK_VERIFY_URL = "https://api.paystack.co/transaction/verify/";

	/**
	 * Initializes a transaction with Paystack using the provided payment request details.
	 *
	 * @param paymentRequest the payment request containing transaction details.
	 * @return the payment response from Paystack.
	 */
	public PaymentResponse initializeTransaction(PaymentRequest paymentRequest) {
		HttpHeaders headers = createHeaders();
		HttpEntity<PaymentRequest> entity = new HttpEntity<>(paymentRequest, headers);

		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(PAYSTACK_INIT_URL, HttpMethod.POST, entity, String.class);
			return objectMapper.readValue(responseEntity.getBody(), PaymentResponse.class);
		} catch (HttpClientErrorException ex) {
			throw new RuntimeException("Error initializing transaction: " + ex.getResponseBodyAsString(), ex);
		} catch (Exception ex) {
			throw new RuntimeException("Unexpected error occurred during transaction initialization", ex);
		}
	}

	/**
	 * Verifies a transaction with Paystack using the provided reference.
	 *
	 * @param reference the transaction reference to verify.
	 * @return the verification result as a String.
	 */
	public String verifyTransaction(String reference) {
		String url = PAYSTACK_VERIFY_URL + reference;
		HttpHeaders headers = createHeaders();
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			return responseEntity.getBody();
		} catch (HttpClientErrorException ex) {
			throw new RuntimeException("Error verifying transaction: " + ex.getResponseBodyAsString(), ex);
		} catch (Exception ex) {
			throw new RuntimeException("Unexpected error occurred during transaction verification", ex);
		}
	}

	/**
	 * Helper method to create standard HTTP headers used for Paystack API calls.
	 *
	 * @return HttpHeaders object containing authorization and content-type headers.
	 */
	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + payStackSecret);
		headers.set("Content-Type", "application/json");
		return headers;
	}
}

