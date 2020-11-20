package com.techelevator.tenmo.services;

import java.math.BigDecimal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.view.ConsoleService;

public class BalanceService {
	
	
	private static final String API_BASE_URL = "http://localhost:8080/";
	private static RestTemplate restTemplate = new RestTemplate();
    private ConsoleService console;
    private AuthenticationService authenticationService;
    
    public void viewCurrentBalance(AuthenticatedUser currentUser) {
		
	BigDecimal myAccount= restTemplate.exchange(API_BASE_URL +"balance", HttpMethod.GET, makeAuthEntity(currentUser), BigDecimal.class).getBody();
	System.out.println("your current balance is: " + myAccount);
}

	private HttpEntity makeAuthEntity(AuthenticatedUser currentUser) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(currentUser.getToken());
		HttpEntity entity = new HttpEntity<>(headers);
		return entity;
	}

	}

