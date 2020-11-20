package com.techelevator.tenmo.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfers;
import com.techelevator.tenmo.models.User;
import com.techelevator.view.ConsoleService;

public class TransferService {
	
	
	private static final String API_BASE_URL = "http://localhost:8080/";
	private static RestTemplate restTemplate = new RestTemplate();
    private ConsoleService console;
    
	public void viewTransferHistory(AuthenticatedUser currentUser) {
		Transfers[] allTransfers = null;
		try {
		  	allTransfers = restTemplate.exchange(API_BASE_URL + "transfers",HttpMethod.GET, makeAuthEntity(currentUser), Transfers[].class ).getBody();
		  	 for (int i =0; i< allTransfers.length; i++) {
		  		 System.out.println("TRANSFERS");
		  		 System.out.print("Transfer ID: " + allTransfers[i].getId() + " | ");
				 System.out.print("Account from: " + allTransfers[i].getFromUser() + " | ");
				 System.out.print("Account to: " + allTransfers[i].getToUser() + " | ");
				 System.out.print("Amount sent: $" + allTransfers[i].getAmount().toString() + " | ");
				
				 System.out.println();
				 
			 }
		  } catch (RestClientResponseException ex) {
		   System.out.println(ex.getRawStatusCode() + " : " + ex.getStatusText());
		  } catch (ResourceAccessException ex) {
		   System.out.println(ex.getMessage());
		  }
			
		  }
	
	public void viewPendingRequests(AuthenticatedUser currentUser) {
		Transfers[] pendingRequests = null;
		
		try {
		  pendingRequests = restTemplate.exchange(API_BASE_URL + "pending_requests",HttpMethod.GET, makeAuthEntity(currentUser), Transfers[].class ).getBody();
		  for (int i =0; i< pendingRequests.length; i++) {
		  		
			  	 System.out.print("Transfer ID: " + pendingRequests[i].getId() + " | ");
				 System.out.print("Account from: " + pendingRequests[i].getFromUser() + " | ");
				 System.out.print("Account to: " + pendingRequests[i].getToUser() + " | ");
				 System.out.print("Amount sent: $" + pendingRequests[i].getAmount().toString() + " | ");
		
				 System.out.println();
		  }
	  	} catch (RestClientResponseException ex) {
		   System.out.println(ex.getRawStatusCode() + " : " + ex.getStatusText());
		} catch (ResourceAccessException ex) {
		   System.out.println(ex.getMessage());
		}
			
	
		}
	public void viewAllUsers(AuthenticatedUser currentUser) {
		User[] allUsers = null;
		try {
		  	allUsers = restTemplate.exchange(API_BASE_URL + "users",HttpMethod.GET, makeAuthEntity(currentUser), User[].class ).getBody();
		  	for (int i =0; i< allUsers.length; i++) {
		  		
		  		 System.out.print("User Id: " + allUsers[i].getId() + " | ");
				 System.out.print("User Name: " + allUsers[i].getUsername() + " | ");
				 System.out.println();
		  	}

		  } catch (RestClientResponseException ex) {
		   System.out.println(ex.getRawStatusCode() + " : " + ex.getStatusText());
		  } catch (ResourceAccessException ex) {
		   System.out.println(ex.getMessage());
		  }
	
		  }
	public void sendBucks(AuthenticatedUser currentUser, int id, BigDecimal amount) {
		Transfers transfer = null;
		
		try {
			transfer = restTemplate.exchange(API_BASE_URL + "getting_bucks/" + id + "/" + amount, HttpMethod.PUT,makeAuthEntity(currentUser), Transfers.class ).getBody();
			System.out.println("APPROVED");
			restTemplate.exchange(API_BASE_URL + "post_to_transfer/" + id + "/" + amount, HttpMethod.POST, makeAuthEntity(currentUser), Transfers.class ).getBody();
	  } catch (RestClientResponseException ex) {
	   System.out.println(ex.getRawStatusCode() + " Try again, either insufficient funds or invalid ID" + ex.getStatusText());
	  } catch (ResourceAccessException ex) {
	   System.out.println(ex.getMessage());
	  }
	}

	public void viewTransferDetailsById(long transferIdDetails, AuthenticatedUser currentUser) {
		Transfers myTransferDetails = null;
		try {
		  	myTransferDetails = restTemplate.exchange(API_BASE_URL + "transfers/" + transferIdDetails, HttpMethod.GET, makeAuthEntity(currentUser), Transfers.class ).getBody();
		  	
		  		System.out.println();
		  		System.out.println("TRANSFER DETAILS");
		  		System.out.println("ID: " + myTransferDetails.getId());
				System.out.println("From: " + myTransferDetails.getFromUser());
		  		System.out.println("To: " + myTransferDetails.getToUser());
		  		System.out.println("Status: " + myTransferDetails.getTransferStatus());
		  		System.out.println("Amount: $" + myTransferDetails.getAmount());
	 
 	
		} catch (RestClientResponseException ex) {
			System.out.println(ex.getRawStatusCode() + " : " + ex.getStatusText() + "Please select a valid option");
		} catch (ResourceAccessException ex) {
			System.out.println(ex.getMessage());
	  }
	}
	
	
	private HttpEntity makeAuthEntity(AuthenticatedUser currentUser) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(currentUser.getToken());
		HttpEntity entity = new HttpEntity<>(headers);
		return entity;
	}
}
