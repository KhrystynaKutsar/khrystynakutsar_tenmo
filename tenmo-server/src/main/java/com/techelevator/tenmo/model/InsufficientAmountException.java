package com.techelevator.tenmo.model;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus( value = HttpStatus.BAD_REQUEST, reason = "Insufficient Amount on your account.")
public class InsufficientAmountException extends RuntimeException {
		 private static final long serialVersionUID = 1L;

		    public InsufficientAmountException() {
		        super("Insufficient Amount on your account.");
		   }
}

