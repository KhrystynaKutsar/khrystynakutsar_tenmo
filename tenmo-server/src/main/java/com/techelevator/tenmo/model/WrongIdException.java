package com.techelevator.tenmo.model;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus( value = HttpStatus.BAD_REQUEST, reason = "Wrong ID Request.")
public class WrongIdException extends Exception {
	 private static final long serialVersionUID = 1L;

	    public WrongIdException() {
	        super("Wrong ID Request.");
	    }
	}